package com.backstage.backstageservice;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.backstage.backstagrepository.MemberRepository;

@Service
public class BackStageAuditOrgServiceImpl implements BackStageAuditOrgService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public ResponseEntity<byte[]> getImgByOrgId(Integer orgId) {
   
        String path = "upload/member/"+memberRepository.findPendingPicByOrgId(orgId);
        String defaultImagePath = "upload/member/FileNotFound.png";
     
        
        // 查詢結果異常時使用預設圖
        if (path == null || path.isEmpty()) {
            path = defaultImagePath;
        }
        File file = new File(path);
    
        // 若指定的圖片不存在，也回傳至預設圖
        if (!file.exists()) {
            file = new File(defaultImagePath);
        }

        try {
            // 讀取檔案內容
            byte[] fileContent = Files.readAllBytes(file.toPath());
            // 嘗試取得 MIME 類型
            String mimeType = Files.probeContentType(file.toPath());
            if (mimeType == null) {
                mimeType = "application/octet-stream"; // fallback
            }

            // 設定回應 headers，說明是甚麼檔案，順便取消下載。
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(mimeType));
            headers.setContentDisposition(ContentDisposition.inline()
            							.filename(file.getName())
            							.build());
            
            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        
    }
  
}
