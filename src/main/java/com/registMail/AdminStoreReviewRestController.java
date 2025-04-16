package com.registMail;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.backstage.backstagrepository.PhotoRepository;
import com.backstage.backstagrepository.StoreRepository;
import com.entity.PhotoVO;
import com.entity.StoreVO;

@RestController
@RequestMapping("/backStage") 
public class AdminStoreReviewRestController {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PhotoRepository photoRepository;

    // 取得所有店家
    @GetMapping("/stores")
    public List<StoreVO> getAllStores() {
        return storeRepository.findAll();
    }

    // 取得待審核的店家
    @GetMapping("/stores/reviewing")
    public List<StoreVO> getPendingStores() {
        return storeRepository.findByReviewedIn(List.of(0, 3));
    }

    // 取得單一店家詳細資料
    @GetMapping("/store/{storeId}")
    public StoreVO getStoreById(@PathVariable int storeId) {
        return storeRepository.findById(storeId).orElseThrow(() -> new RuntimeException("找不到店家資料"));
    }

    // 取得店家照片（轉 base64）
    @GetMapping("/store/{storeId}/photos")
    public Map<String, String> getStorePhotos(@PathVariable int storeId) {
        List<PhotoVO> photos = photoRepository.findByStoreStoreId(storeId);
        Map<String, String> result = new HashMap<>();

        for (PhotoVO photo : photos) {
            if (photo.getPhotoSrc() != null) {
                String base64 = Base64.getEncoder().encodeToString(photo.getPhotoSrc());
                String dataUri = "data:image/jpeg;base64," + base64;

                switch (photo.getPhotoType().toLowerCase()) {
                    case "cover" -> result.put("coverPhotoUrl", dataUri);
                    case "kitchen" -> result.put("kitchenPhotoUrl", dataUri);
                    case "store" -> result.put("storePhotoUrl", dataUri);
                }
            }
        }

        return result;
    }

    // 店家審核（通過/退件/補件）
    @PutMapping("/store/{storeId}/review")
    public String reviewStore(@PathVariable int storeId, @RequestParam String approved) {
        Optional<StoreVO> storeOpt = storeRepository.findById(storeId);
        if (storeOpt.isEmpty()) {
            return "店家不存在";
        }

        StoreVO store = storeOpt.get();
        switch (approved) {
            case "true":
                store.setReviewed(1);
                emailService.sendVerificationEmail(store);
                break;
            case "false":
                store.setReviewed(2);
                emailService.sendRejectionEmail(store);
                break;
            case "correction":
                store.setReviewed(0);
                emailService.sendCorrectionEmail(store);
                break;
            default:
                return "審核參數無效";
        }

        storeRepository.save(store);
        return "店家審核完成";
    }

    // 切換啟用/停用狀態
    @PutMapping("/store/{storeId}/toggleStatus")
    public String toggleStoreStatus(@PathVariable int storeId) {
        Optional<StoreVO> storeOpt = storeRepository.findById(storeId);
        if (storeOpt.isEmpty()) {
            return "找不到店家";
        }

        StoreVO store = storeOpt.get();

        if (store.getReviewed() != 1) {
            return "僅已審核通過的店家可以切換啟用狀態";
        }

        if (store.getAccStat() == 1) {
            store.setAccStat(2);
        } else if (store.getAccStat() == 2) {
            store.setAccStat(1);
        } else {
            return "帳號狀態異常";
        }

        storeRepository.save(store);
        return "店家帳號狀態已切換為 " + (store.getAccStat() == 1 ? "啟用中" : "停用中");
    }
}
