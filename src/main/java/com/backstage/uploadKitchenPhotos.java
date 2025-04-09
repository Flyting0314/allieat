package com.backstage;

import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.backstage.backstagrepository.PhotoRepository;
import com.backstage.backstagrepository.StoreRepository;
import com.entity.PhotoVO;
import com.entity.StoreVO;

import jakarta.annotation.PostConstruct;
/*
 * 啟動時新增Photo Table的照片假資料，照片資源放置於 
 * static/storephoto 若不需要可以移除本檔案與storephoto
 */
@Component
public class uploadKitchenPhotos {
    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private StoreRepository storeRepository;

    @PostConstruct
    public void uploadKitchenPhotos() throws java.io.IOException {
        for (int i = 1; i <= 10; i++) {
            try {
                // 讀取圖片檔案
                ClassPathResource imgFile = new ClassPathResource("static/storephoto/" + i + ".jpg");
                byte[] imageBytes = Files.readAllBytes(imgFile.getFile().toPath());

                // 找對應的 StoreVO
                StoreVO store = storeRepository.findById(i).orElse(null);
                if (store == null) {
                    System.out.println("StoreId " + i + " not found, skip.");
                    continue;
                }

                // 嘗試取得已存在的 PhotoVO
                PhotoVO photo = photoRepository.findById(i).orElse(new PhotoVO());
                photo.setPhotoId(i); // 指定 primary key（如果是新建的）
                photo.setStore(store);
                photo.setPhotoSrc(imageBytes);
                photo.setPhotoType("KITCHEN");
                photo.setUpdateTime(Timestamp.valueOf(LocalDateTime.of(2025, 2, 19, 10, (i - 1) * 5)));

                photoRepository.save(photo);

            } catch (Exception e) {
                System.err.println("Failed to handle image " + i + ".jpg");
                e.printStackTrace();
            }
        }

        System.out.println("All 10 kitchen photos uploaded or updated.");
    }
}
