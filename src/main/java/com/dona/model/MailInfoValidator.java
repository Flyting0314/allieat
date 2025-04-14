package com.dona.model;

import org.springframework.util.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MailInfoValidator implements ConstraintValidator<ValidMailInfo, DonaReq> {

	@Override
	public boolean isValid(DonaReq donaReq, ConstraintValidatorContext context) {
	    if (donaReq == null || donaReq.getMailMtd() == null || donaReq.getMailMtd() != 0) {
	        return true; // 不寄送收據則不驗證
	    }

	    boolean valid = true;
	    context.disableDefaultConstraintViolation();

	 // 抬頭驗證
        if (!StringUtils.hasText(donaReq.getSalutation())) {
            context.buildConstraintViolationWithTemplate("抬頭為必填")
                   .addPropertyNode("salutation").addConstraintViolation();
            valid = false;
        } else if (!donaReq.getSalutation().matches("^[\\u4e00-\\u9fa5a-zA-Z0-9_]{2,10}$")) {
            context.buildConstraintViolationWithTemplate("抬頭格式錯誤，限 2-10 字的中英文與數字")
                   .addPropertyNode("salutation").addConstraintViolation();
            valid = false;
        }


	    // ID 或 GUI 格式
	    boolean hasId = StringUtils.hasText(donaReq.getIdNum());
	    boolean hasGui = StringUtils.hasText(donaReq.getGuiNum());

	    if (!hasId && !hasGui) {
	        context.buildConstraintViolationWithTemplate("請填寫身分證")
	               .addPropertyNode("idNum").addConstraintViolation();
	        context.buildConstraintViolationWithTemplate("請填寫統一編號")
	           .addPropertyNode("guiNum").addConstraintViolation(); 
	        valid = false;
	    } else {
	        if (hasId && !donaReq.getIdNum().matches("^[A-Z][0-9]{9}$")) {
	            context.buildConstraintViolationWithTemplate("身分證格式錯誤（例：A123456789）")
	                   .addPropertyNode("idNum").addConstraintViolation();
	            valid = false;
	        }
	        if (hasGui && !donaReq.getGuiNum().matches("^\\d{8}$")) {
	            context.buildConstraintViolationWithTemplate("統一編號需為 8 位數字")
	                   .addPropertyNode("guiNum").addConstraintViolation();
	            valid = false;
	        }
	    }

	    // 地址欄位
	    if (!StringUtils.hasText(donaReq.getCounty())) {
	        context.buildConstraintViolationWithTemplate("請選擇縣市")
	               .addPropertyNode("county").addConstraintViolation();
	        valid = false;
	    }

	    if (!StringUtils.hasText(donaReq.getDistrict())) {
	        context.buildConstraintViolationWithTemplate("請選擇鄉鎮市區")
	               .addPropertyNode("district").addConstraintViolation();
	        valid = false;
	    }

	    if (donaReq.getPostalCode() == null) {
	        context.buildConstraintViolationWithTemplate("請選擇郵遞區號")
	               .addPropertyNode("postalCode").addConstraintViolation();
	        valid = false;
	    }

	    if (!StringUtils.hasText(donaReq.getAddress())) {
	        context.buildConstraintViolationWithTemplate("請填寫詳細地址")
	               .addPropertyNode("address").addConstraintViolation();
	        valid = false;
	    }

	    return valid;
	}

}
