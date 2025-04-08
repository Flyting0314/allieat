package com.backstage.backstagedto;

import java.util.List;

public class DonateInitResponse {
    private List<DonateDTO> donationList;
    private String errormessage;

    public DonateInitResponse(List<DonateDTO> donationList) {
        this.donationList = donationList;
    }
    public DonateInitResponse(String errormessage) {
        this.errormessage = errormessage;
    }

    public DonateInitResponse() {
    }

    public List<DonateDTO> getDonationList() {
        return donationList;
    }

    public void setDonationList(List<DonateDTO> donationList) {
        this.donationList = donationList;
    }

    public String getErrormessage() {
        return errormessage;
    }

    public void setErrormessage(String errormessage) {
        this.errormessage = errormessage;
    }


}
