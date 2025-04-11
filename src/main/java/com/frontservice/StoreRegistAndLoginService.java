package com.frontservice;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import com.backstage.backstagrepository.PhotoRepository;
import com.backstage.backstagrepository.StoreRepository;
import com.entity.PhotoVO;
import com.entity.StoreVO;

import jakarta.servlet.http.HttpSession;
@Service
public class StoreRegistAndLoginService {
    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private JavaMailSender mailSender;

    public StoreVO prepareStoreForSession(StoreVO store, MultipartFile[] photoFiles, String agreedToTerms) throws IOException {
        if (!"true".equals(agreedToTerms)) {
            throw new IllegalArgumentException("請詳閱並同意使用須知");
        }

        if (storeRepository.existsByEmail(store.getEmail())) {
            throw new IllegalArgumentException("信箱已註冊，請使用其他信箱");
        }

        Set<PhotoVO> photoSet = mergePhotos(store, photoFiles);

        if (photoSet.size() < 3) {
            throw new IllegalArgumentException("請上傳至少三張照片");
        }

        store.setStoreToPhoto(photoSet);
        store.setRegTime(new Timestamp(System.currentTimeMillis()));
        store.setAccStat(0); // 尚未啟用
        store.setReviewed(3); // 未審核
        store.setOpStat(0); // 暫不開放
        store.setVerificationMail(UUID.randomUUID().toString());
        return store;
    }
    public Set<PhotoVO> mergePhotos(StoreVO store, MultipartFile[] photoFiles) throws IOException {
        Set<PhotoVO> photoSet = store.getStoreToPhoto() != null ? new HashSet<>(store.getStoreToPhoto()) : new HashSet<>();
        String[] types = {"COVER", "KITCHEN", "STORE"};
        int i = 0;

        for (MultipartFile file : photoFiles) {
            if (file != null && !file.isEmpty()) {
                String type = (i < types.length) ? types[i] : "OTHER";

                Optional<PhotoVO> existingPhoto = photoSet.stream()
                    .filter(p -> p.getPhotoType().equals(type))
                    .findFirst();

                if (existingPhoto.isPresent()) {
                    existingPhoto.get().setPhotoSrc(file.getBytes()); // 更新現有圖
                    existingPhoto.get().setUpdateTime(new Timestamp(System.currentTimeMillis()));
                } else {
                    PhotoVO photo = new PhotoVO();
                    photo.setPhotoSrc(file.getBytes());
                    photo.setStore(store);
                    photo.setPhotoType(type);
                    photoSet.add(photo);
                }
            }
            i++;
        }
        store.setStoreToPhoto(photoSet); 
        return photoSet;
    }

    public void generatePhotoPreview(StoreVO store, HttpSession session, Model model) {
        List<Map<String, String>> base64Photos = new ArrayList<>();

        if (store.getStoreToPhoto() != null && !store.getStoreToPhoto().isEmpty()) {
            for (PhotoVO photo : store.getStoreToPhoto()) {
                Map<String, String> map = new HashMap<>();
                String base64 = Base64.getEncoder().encodeToString(photo.getPhotoSrc());
                map.put("photoType", photo.getPhotoType());
                map.put("src", "data:image/png;base64," + base64);
                base64Photos.add(map);
            }
        } 
        session.setAttribute("photoList", base64Photos);
        model.addAttribute("photoList", base64Photos);
    }
    
    // 審核人員通過後
    public void approveStoreAndSendEmail(Integer storeId) {
        Optional<StoreVO> opt = storeRepository.findById(storeId);
        if (opt.isPresent()) {
            StoreVO store = opt.get();

            store.setReviewed(1); // 設已審核

            // 產生驗證碼與驗證信
            String token = UUID.randomUUID().toString();
            store.setVerificationMail(token);
            storeRepository.save(store);

            String verifyUrl = "http://localhost:8080/registerAndLogin/verify?token=" + token;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(store.getEmail());
            message.setSubject("【攏呷霸 ALLiEAT】店家帳號驗證信");
            message.setText("親愛的 " + store.getName() + " 店家您好：\n\n"
                    + "您在攏呷霸的註冊已審核通過，請點擊以下連結完成帳號啟用：\n"
                    + verifyUrl + "\n\n"
                    + "如您未曾申請此帳號，可忽略此封信件。\n\n"
                    + "-- 攏呷霸 ALLiEAT 團隊");

            mailSender.send(message);
        }
    }

    public boolean activateAccountByToken(String token) {
        Optional<StoreVO> opt = storeRepository.findByVerificationMail(token);
        if (opt.isPresent()) {
            StoreVO store = opt.get();
            store.setAccStat(1);           // 設啟用
            store.setVerificationMail(null); // 清除驗證碼
            storeRepository.save(store);
            return true;
        }
        return false;
    }

    public void finalizeRegistration(StoreVO store) {
        storeRepository.save(store);
        if (store.getStoreToPhoto() != null) {
            photoRepository.saveAll(store.getStoreToPhoto());
        }
    }

    public boolean isStoreMissingOpeningInfo(StoreVO store) {
        return store.getOpTime() == null || store.getCloseTime() == null ||
               store.getLastOrder() == null || store.getPickTime() == null;
    }

    public StoreVO getOneStore(Integer id) {
        return storeRepository.findById(id).orElse(null);
    }
}