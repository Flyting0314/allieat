package com.frontservice;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
    @Autowired
  private JavaMailSender mailSender;
    public StoreVO prepareStoreForSession(StoreVO store, MultipartFile[] photoFiles, String agreedToTerms) throws IOException {
        if (!"true".equals(agreedToTerms)) {
            throw new IllegalArgumentException("請詳閱並同意使用須知");
        }

        if (storeRepository.existsByEmail(store.getEmail())) {
            throw new IllegalArgumentException("信箱已註冊，請使用其他信箱");
        }

        Set<PhotoVO> photoSet = new HashSet<>();
        String[] types = {"COVER", "KITCHEN", "STORE"};
        int i = 0;
        for (MultipartFile file : photoFiles) {
            if (file != null && !file.isEmpty()) {
                PhotoVO photo = new PhotoVO();
                photo.setPhotoSrc(file.getBytes());
                photo.setStore(store);
                photo.setPhotoType((i < types.length) ? types[i] : "OTHER");
                photoSet.add(photo);
                i++;
            }
        }

        if (photoSet.isEmpty()) {
            throw new IllegalArgumentException("請上傳至少三張照片");
        }

        store.setStoreToPhoto(photoSet);
        store.setRegTime(new Timestamp(System.currentTimeMillis()));
        store.setAccStat(0); // 尚未啟用
        store.setReviewed(3); // 未審核
        store.setOpStat(0);
        store.setVerificationMail(UUID.randomUUID().toString());
        return store;
    }
 // ✅ 審核人員通過後呼叫此方法
  public void approveStoreAndSendEmail(Integer storeId) {
  	Optional<StoreVO> opt = storeRepository.findById(storeId);
      if (opt.isPresent()) {
          StoreVO store = opt.get();

          store.setReviewed(1); // 設為已審核通過

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
          store.setAccStat(1);           // 設為啟用
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
 // // ✅ 審核人員通過後呼叫此方法
//  public void approveStoreAndSendEmail(Integer storeId) {
//  	Optional<StoreVO> opt = storeRepository.findById(storeId);
//      if (opt.isPresent()) {
//          StoreVO store = opt.get();
//
//          store.setReviewed(1); // 設為已審核通過
//
//          // 產生驗證碼與驗證信
//          String token = UUID.randomUUID().toString();
//          store.setVerificationMail(token);
//          storeRepository.save(store);
//
//          String verifyUrl = "http://localhost:8080/registerAndLogin/verify?token=" + token;
//
//          SimpleMailMessage message = new SimpleMailMessage();
//          message.setTo(store.getEmail());
//          message.setSubject("【攏呷霸 ALLiEAT】店家帳號驗證信");
//          message.setText("親愛的 " + store.getName() + " 店家您好：\n\n"
//                  + "您在攏呷霸的註冊已審核通過，請點擊以下連結完成帳號啟用：\n"
//                  + verifyUrl + "\n\n"
//                  + "如您未曾申請此帳號，可忽略此封信件。\n\n"
//                  + "-- 攏呷霸 ALLiEAT 團隊");
//
//          mailSender.send(message);
//      }
//  
//  }
//  public boolean activateAccountByToken(String token) {
//      Optional<StoreVO> opt = storeRepository.findByVerificationMail(token);
//      if (opt.isPresent()) {
//          StoreVO store = opt.get();
//          store.setAccStat(1);           // 設為啟用
//          store.setVerificationMail(null); // 清除驗證碼
//          storeRepository.save(store);
//          return true;
//      }
//      return false;
//  }
//// ✅ 登入後檢查是否需導向設定營業時間
//  public boolean isStoreMissingOpeningInfo(StoreVO store) {
//      return store.getOpTime() == null ||
//             store.getCloseTime() == null ||
//             store.getLastOrder() == null ||
//             store.getPickTime() == null;
//  }
//  public void finalizeRegistration(StoreVO store) {
//  	 // 初始化狀態
//      store.setAccStat(0);          // 尚未啟用
//      store.setReviewed(3);         // 未審核
//      store.setVerificationMail(UUID.randomUUID().toString());
//  	// 儲存店家與圖片資訊
//      storeRepository.save(store);
//      if (store.getStoreToPhoto() != null) {
//          photoRepository.saveAll(store.getStoreToPhoto());
//      }
//  }
//
//  public StoreVO getOneStore(Integer id) {
//      return storeRepository.findById(id).orElse(null);
//  }
//
//  public void deleteStore(Integer id) {
//      if (storeRepository.existsById(id)) {
//          storeRepository.deleteById(id);
//      }
//  }
//  
//  public List<PhotoVO> getStorePhotosByOrder(StoreVO store) {
//      return photoRepository.findByStoreOrderByPhotoIdAsc(store);
//  }
//
//}
}
//@Service
//public class StoreRegistAndLoginService {
//    @Autowired
//    private StoreRepository storeRepository;
//
//    @Autowired
//    private PhotoRepository photoRepository;
//    
//    @Autowired
//    private JavaMailSender mailSender;
//    public StoreVO prepareStoreForSession(StoreVO store, MultipartFile[] photoFiles, String agreedToTerms) throws IOException {
//        if (!"true".equals(agreedToTerms)) {
//            throw new IllegalArgumentException("請詳閱並同意使用須知");
//        }
//
//        if (storeRepository.existsByEmail(store.getEmail())) {
//            throw new IllegalArgumentException("信箱已註冊，請使用其他信箱");
//        }
//
//        Set<PhotoVO> photoSet = new HashSet<>();
//        String[] types = {"COVER", "KITCHEN", "STORE"};
//        int i = 0;
//        for (MultipartFile file : photoFiles) {
//            if (file != null && !file.isEmpty()) {
//                PhotoVO photo = new PhotoVO();
//                photo.setPhotoSrc(file.getBytes());
//                photo.setStore(store);
//                String type = (i < types.length) ? types[i] : "OTHER";
//                photo.setPhotoType(type);
//                photoSet.add(photo);
//                i++;
//            }
//        }
//
//        if (photoSet.isEmpty()) {
//            throw new IllegalArgumentException("請上傳至少三張照片");
//        }
//
//        store.setStoreToPhoto(photoSet);
//        store.setRegTime(new Timestamp(System.currentTimeMillis()));
//        if (store.getAccStat() == null) store.setAccStat(0);
//        if (store.getReviewed() == null) store.setReviewed(3);
//        if (store.getOpStat() == null) store.setOpStat(0);
//        store.setVerificationMail(UUID.randomUUID().toString());
//        return store;
//    }
//
//
//
// // ✅ 審核人員通過後呼叫此方法
//    public void approveStoreAndSendEmail(Integer storeId) {
//    	Optional<StoreVO> opt = storeRepository.findById(storeId);
//        if (opt.isPresent()) {
//            StoreVO store = opt.get();
//
//            store.setReviewed(1); // 設為已審核通過
//
//            // 產生驗證碼與驗證信
//            String token = UUID.randomUUID().toString();
//            store.setVerificationMail(token);
//            storeRepository.save(store);
//
//            String verifyUrl = "http://localhost:8080/registerAndLogin/verify?token=" + token;
//
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setTo(store.getEmail());
//            message.setSubject("【攏呷霸 ALLiEAT】店家帳號驗證信");
//            message.setText("親愛的 " + store.getName() + " 店家您好：\n\n"
//                    + "您在攏呷霸的註冊已審核通過，請點擊以下連結完成帳號啟用：\n"
//                    + verifyUrl + "\n\n"
//                    + "如您未曾申請此帳號，可忽略此封信件。\n\n"
//                    + "-- 攏呷霸 ALLiEAT 團隊");
//
//            mailSender.send(message);
//        }
//    
//    }
//    public boolean activateAccountByToken(String token) {
//        Optional<StoreVO> opt = storeRepository.findByVerificationMail(token);
//        if (opt.isPresent()) {
//            StoreVO store = opt.get();
//            store.setAccStat(1);           // 設為啟用
//            store.setVerificationMail(null); // 清除驗證碼
//            storeRepository.save(store);
//            return true;
//        }
//        return false;
//    }
// // ✅ 登入後檢查是否需導向設定營業時間
//    public boolean isStoreMissingOpeningInfo(StoreVO store) {
//        return store.getOpTime() == null ||
//               store.getCloseTime() == null ||
//               store.getLastOrder() == null ||
//               store.getPickTime() == null;
//    }
//    public void finalizeRegistration(StoreVO store) {
//    	 // 初始化狀態
//        store.setAccStat(0);          // 尚未啟用
//        store.setReviewed(3);         // 未審核
//        store.setVerificationMail(UUID.randomUUID().toString());
//    	// 儲存店家與圖片資訊
//        storeRepository.save(store);
//        if (store.getStoreToPhoto() != null) {
//            photoRepository.saveAll(store.getStoreToPhoto());
//        }
//    }
//
//    public StoreVO getOneStore(Integer id) {
//        return storeRepository.findById(id).orElse(null);
//    }
//
//    public void deleteStore(Integer id) {
//        if (storeRepository.existsById(id)) {
//            storeRepository.deleteById(id);
//        }
//    }
//    
//    public List<PhotoVO> getStorePhotosByOrder(StoreVO store) {
//        return photoRepository.findByStoreOrderByPhotoIdAsc(store);
//    }
//
//}
////public StoreVO prepareStoreForSession(StoreVO store, MultipartFile[] photoFiles, String agreedToTerms) throws IOException {
////if (!"true".equals(agreedToTerms)) {
////  throw new IllegalArgumentException("請詳閱並同意使用須知");
////}
////
////if (storeRepository.existsByEmail(store.getEmail())) {
////  throw new IllegalArgumentException("信箱已註冊，請使用其他信箱");
////}
//
//===============================================
//// 儲存圖片資訊至 PhotoVO (byte[] 儲存)
//Set<PhotoVO> photoSet = new HashSet<>();
//for (MultipartFile file : photoFiles) {
//  if (file != null && !file.isEmpty()) {
//      PhotoVO photo = new PhotoVO();
//      photo.setPhotoSrc(file.getBytes());
//      photo.setStore(store); // 尚未存進 DB，但已建立雙向關聯
//      photoSet.add(photo);
//  }
//}
//// ✅ 如果使用者沒有上傳照片，仍然建立空集合
//if (photoSet.isEmpty()) {
//  throw new IllegalArgumentException("請上傳至少三張照片");
//}
//
//store.setStoreToPhoto(photoSet);
//
//// 預設值初始化
//store.setRegTime(new Timestamp(System.currentTimeMillis()));
//if (store.getAccStat() == null) store.setAccStat(0);
//if (store.getReviewed() == null) store.setReviewed(3);
//if (store.getOpStat() == null) store.setOpStat(0);
//
//return store;
//}