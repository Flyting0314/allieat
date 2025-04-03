package com.frontservice;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.backstage.backstagrepository.PhotoRepository;
import com.backstage.backstagrepository.StoreRepository;
import com.entity.PhotoVO;
import com.entity.StoreVO;

@Service
public class StoreRegistAndLoginService {
    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private PhotoRepository photoRepository;
    


    public StoreVO prepareStoreForSession(StoreVO store, MultipartFile[] photoFiles, String agreedToTerms) throws IOException {
        if (!"true".equals(agreedToTerms)) {
            throw new IllegalArgumentException("請詳閱並同意使用須知");
        }

        if (storeRepository.existsByEmail(store.getEmail())) {
            throw new IllegalArgumentException("信箱已註冊，請使用其他信箱");
        }


     // 儲存圖片資訊至 PhotoVO (byte[] 儲存)
        Set<PhotoVO> photoSet = new HashSet<>();
        for (MultipartFile file : photoFiles) {
            if (file != null && !file.isEmpty()) {
                PhotoVO photo = new PhotoVO();
                photo.setPhotoSrc(file.getBytes());
                photo.setStore(store); // 尚未存進 DB，但已建立雙向關聯
                photoSet.add(photo);
            }
        }
     // ✅ 如果使用者沒有上傳照片，仍然建立空集合
        if (photoSet.isEmpty()) {
            throw new IllegalArgumentException("請上傳至少三張照片");
        }

        store.setStoreToPhoto(photoSet);

        // 預設值初始化
        store.setRegTime(new Timestamp(System.currentTimeMillis()));
        if (store.getAccStat() == null) store.setAccStat(0);
        if (store.getReviewed() == null) store.setReviewed(3);
        if (store.getOpStat() == null) store.setOpStat(0);

        return store;
    }

    public void finalizeRegistration(StoreVO store) {
        // 儲存店家與圖片資訊
        storeRepository.save(store);
        if (store.getStoreToPhoto() != null) {
            photoRepository.saveAll(store.getStoreToPhoto());
        }
    }

    public StoreVO getOneStore(Integer id) {
        return storeRepository.findById(id).orElse(null);
    }

    public void deleteStore(Integer id) {
        if (storeRepository.existsById(id)) {
            storeRepository.deleteById(id);
        }
    }
}
