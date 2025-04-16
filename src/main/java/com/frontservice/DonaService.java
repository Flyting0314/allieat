package com.frontservice;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backstage.backstagrepository.DonorRepository;
import com.dona.model.DonaReq;
import com.entity.DonaVO;



@Service("donaService")
public class DonaService {

    @Autowired
    private DonorRepository donorRepository;

    // 查詢所有紀錄
    public List<DonaVO> getAllDonas() {
        return donorRepository.findAll(); // 使用 JPA 查詢所有紀錄
    }

    // 根據 donationRecordId 查詢單筆紀錄
    public DonaVO findById(Integer donationRecordId) {
        return donorRepository.findById(donationRecordId).orElse(null);
    }

    // 新增捐款紀錄
    public DonaVO insertDona(DonaReq donaReq) {
    	DonaVO dona = convertToDona(donaReq); // 轉換 DonaReq 為 DonaVO
        dona.setCreatedTime(new Timestamp(System.currentTimeMillis())); // 設置建立時間
        return donorRepository.save(dona); // 儲存到資料庫
    }

    // 更新捐款紀錄
    public DonaVO updateDona(Integer donationRecordId, DonaReq donaReq) {
        return donorRepository.findById(donationRecordId).map(existingdonorRepository -> {
            updateFromDonaReq(existingdonorRepository, donaReq); // 更新資料欄位
            existingdonorRepository.setCreatedTime(new Timestamp(System.currentTimeMillis())); // 更新時間
            return donorRepository.save(existingdonorRepository); // 保存更新後的資料
        }).orElse(null);
    }

    // 刪除捐款紀錄
    public void deleteDona(Integer donationRecordId) {
    	donorRepository.deleteById(donationRecordId); // 使用 JPA 刪除紀錄
    }

    // 將 DonaReq 轉換為 DonaVO
    private DonaVO convertToDona(DonaReq donaReq) {
    	DonaVO dona = new DonaVO();
        dona.setIdentityData(donaReq.getIdentityData());
        dona.setDonationIncome(donaReq.getDonationIncome());
        dona.setEmail(donaReq.getEmail());
        dona.setPhone(donaReq.getPhone());
        dona.setBthDate(donaReq.getBthDate());
        dona.setGender(donaReq.getGender());
        dona.setCounty(donaReq.getCounty());
        dona.setDistrict(donaReq.getDistrict());
        dona.setPostalCode(donaReq.getPostalCode());
        dona.setAddress(donaReq.getAddress());
        dona.setMailMtd(donaReq.getMailMtd());
        dona.setSalutation(donaReq.getSalutation());
        dona.setIdNum(donaReq.getIdNum());
        dona.setGuiNum(donaReq.getGuiNum());
        dona.setAnonymous(donaReq.getAnonymous());
        dona.setDonationType(donaReq.getDonationType());
        return dona;
    }

    // 更新已有的 DonaVO 實體
    private void updateFromDonaReq(DonaVO dona, DonaReq donaReq) {
        dona.setIdentityData(donaReq.getIdentityData());
        dona.setDonationIncome(donaReq.getDonationIncome());
        dona.setEmail(donaReq.getEmail());
        dona.setPhone(donaReq.getPhone());
        dona.setBthDate(donaReq.getBthDate());
        dona.setGender(donaReq.getGender());
        dona.setCounty(donaReq.getCounty());
        dona.setDistrict(donaReq.getDistrict());
        dona.setPostalCode(donaReq.getPostalCode());
        dona.setAddress(donaReq.getAddress());
        dona.setMailMtd(donaReq.getMailMtd());
        dona.setSalutation(donaReq.getSalutation());
        dona.setIdNum(donaReq.getIdNum());
        dona.setGuiNum(donaReq.getGuiNum());
        dona.setAnonymous(donaReq.getAnonymous());
        dona.setDonationType(donaReq.getDonationType());
    }
    
 // 生日邏輯驗證
        public String formatBthDate(Integer year, Integer month, Integer day) {
            if (year != null && month != null && day != null) {
                return String.format("%04d-%02d-%02d", year, month, day);
            }
            return null;
        }
        
     // 信用卡驗證邏輯
        public boolean isValidCard(String cardNumber, String cardExpiry, String cardCvv) {
            // 簡單格式驗證：檢查卡號、到期日和 CVV 格式是否正確
            return cardNumber.matches("\\d{16}") && cardExpiry.matches("\\d{2}/\\d{2}") && cardCvv.matches("\\d{3}");
        }
        
     // 搜索捐款紀錄（根據條件篩選）
        public List<DonaVO> searchDonas(DonaReq donaReq, Timestamp startTime, Timestamp endTime) {
        	if (donaReq.getSalutation() == null || donaReq.getSalutation().isBlank()) {
                return List.of(); // 必填：捐款抬頭
            }

            // 兩種身分擇一即可（符合其中一個）
            if ((donaReq.getIdNum() == null || donaReq.getIdNum().isBlank()) &&
                (donaReq.getGuiNum() == null || donaReq.getGuiNum().isBlank())) {
                return List.of(); // 沒有填身分證也沒填統編，視為無效查詢
            }

            return donorRepository.findBySalutationAndIdOrGui(
                    donaReq.getSalutation(),
                    donaReq.getIdNum(),
                    donaReq.getGuiNum(),
                    startTime,
                    endTime
            );
        }


    
     
        public List<DonaVO> findByEmailAndPhone(String email, String phone) {
            return donorRepository.findByEmailAndPhone(email, phone);
        }
        
        public List<DonaVO> findDonations(String email, String phone, Integer startYear, Integer startMonth, Integer endYear, Integer endMonth) {
            Timestamp startTime = createTimestamp(startYear, startMonth, 1);
            Timestamp endTime = createTimestamp(endYear, endMonth, 31);
            return donorRepository.findByEmailAndPhoneWithOptionalDate(email, phone, startTime, endTime);
        }

        public Timestamp createTimestamp(Integer year, Integer month, Integer day) {
            if (year != null && month != null && day != null) {
                return Timestamp.valueOf(String.format("%04d-%02d-%02d 00:00:00", year, month, day));
            }
            return null;
        }
}