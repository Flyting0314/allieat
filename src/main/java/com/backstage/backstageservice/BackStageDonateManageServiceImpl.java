package com.backstage.backstageservice;

import com.backstage.backstagedto.DonateDTO;
import com.backstage.backstagedto.DonateInitResponse;
import com.backstage.backstagrepository.DonorRepository;
import com.entity.DonaVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BackStageDonateManageServiceImpl implements BackStageDonateManageService {
    @Autowired
    private DonorRepository donorRepository;

    @Override
    public ResponseEntity<DonateInitResponse> getInitData() {
        try {
            // 從資料庫獲取所有捐款紀錄
            List<DonaVO> initData = donorRepository.findAll();
            //創建回傳用List
            List<DonateDTO> donateDTOList = new ArrayList<>();
            for (DonaVO donaVO : initData) {
                DonateDTO donateDTO = new DonateDTO();
                //資料mapping
                BeanUtils.copyProperties(donaVO, donateDTO);
                if (donaVO.getGuiNum() != null) {
                    //統一編號設定進idNum裡面，配合前端需求。
                    donateDTO.setIdNum(donaVO.getGuiNum());
                }
                //資料加入回傳陣列
                donateDTOList.add(donateDTO);
            }
            //配合前端需求，使用包裝類別統一 key 名稱 為 donationList
            return ResponseEntity.ok(new DonateInitResponse(donateDTOList));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            //配合前端錯誤處理，回傳錯誤訊息。
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new DonateInitResponse("服務異常請稍後再試"));
        }
    }
}
