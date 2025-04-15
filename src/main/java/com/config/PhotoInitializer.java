package com.config;

import java.io.File;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.backstage.backstagrepository.PhotoRepository;
import com.entity.PhotoVO;

@Component
public class PhotoInitializer implements CommandLineRunner {

    @Autowired
    private PhotoRepository photoRepository;

    @Value("classpath:/static/photoStore")
    private Resource photoFolder;

    @Override
    @Transactional
    public void run(String... args) {
        try {
            File folder = photoFolder.getFile();
            File[] photoFiles = folder.listFiles();

            if (photoFiles == null || photoFiles.length == 0) {
                System.out.println("沒有圖片上傳");
                return;
            }

            Arrays.sort(photoFiles, Comparator.comparing(File::getName));
            int count = 1;

            for (File file : photoFiles) {
                Optional<PhotoVO> photoOpt = photoRepository.findById(count);
                if (photoOpt.isPresent()) {
                    PhotoVO photo = photoOpt.get();
                    photo.setPhotoSrc(Files.readAllBytes(file.toPath()));
                    photo.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                    photoRepository.save(photo);
                }
                count++;
            }

            System.out.println("自動更新成功！");
        } catch (Exception e) {
            System.err.println("錯誤：" + e.getMessage());
        }
    }
}
