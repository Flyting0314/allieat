package com.backstage.backstageservice;

import com.backstage.backstagedto.DonateInitResponse;
import com.backstage.backstagedto.DonationDetailsDTO;


public interface BackStageDonateManageService {
    DonateInitResponse getInitData();
    DonationDetailsDTO getDonateRecord(Integer id);
}
