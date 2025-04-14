package com.backstage.backstageservice;

import com.backstage.backstagedto.DonateDTO;
import com.backstage.backstagedto.DonateInitResponse;
import com.backstage.backstagedto.DonationDetailsDTO;
import com.backstage.backstagrepository.DonorRepository;
import com.entity.DonaVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BackStageDonateManageServiceImpl implements BackStageDonateManageService {
    @Autowired
    private DonorRepository donorRepository;

	@Override
	public DonateInitResponse getInitData() {
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
        return new DonateInitResponse(donateDTOList);
	}

	@Override
	public DonationDetailsDTO getDonateRecord(Integer id) {
		 Optional<DonaVO> data = donorRepository.findById(id);
	        DonationDetailsDTO donationDetailsDTO = new DonationDetailsDTO();
	        if (data.isPresent()) {
	            DonaVO donaVO = data.get();
	            donationDetailsDTO.setDonorName(donaVO.getIdentityData());
	            donationDetailsDTO.setDonationAmount(donaVO.getDonationIncome());
	            donationDetailsDTO.setDonationTime(donaVO.getCreatedTime());
	            donationDetailsDTO.setContactEmail(donaVO.getEmail());
	            donationDetailsDTO.setContactPhone(donaVO.getPhone());
	            //存入身分證字號或統一編號
	            donationDetailsDTO.setIdentityNumber(donaVO.getGuiNum()!=null ? donaVO.getGuiNum():donaVO.getIdNum());
	            //判斷身分為法人還自然人
	            donationDetailsDTO.setIdentityType(donaVO.getGuiNum()!=null ? Boolean.FALSE:Boolean.TRUE);
	            //設定地址
	            String address = detailsAddress(donaVO.getCounty(),
	                    donaVO.getDistrict(),
	                    donaVO.getAddress());
	            donationDetailsDTO.setAddress(address);
	        } 
	        return donationDetailsDTO;
	    }

	    private String detailsAddress(String county, String district, String address) {
	        StringBuilder sb = new StringBuilder();
	        sb.append(county).append("-").append(district).append("-").append(address);
	        return sb.toString();
	    }
}
