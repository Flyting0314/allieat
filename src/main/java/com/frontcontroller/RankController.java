package com.frontcontroller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.frontservice.RankService;

@Controller
@RequestMapping("/rank")
public class RankController {

    @Autowired
    private RankService rankService;

    @GetMapping("/top")
    public String showRanks(Model model) {
    	model.addAttribute("personalTopList", rankService.getPersonalRanks());
        model.addAttribute("companyTopList", rankService.getCompanyRanks());
        model.addAttribute("monthlyPersonalTopList", rankService.getMonthlyPersonalRanks());
        model.addAttribute("monthlyCompanyTopList", rankService.getMonthlyCompanyRanks());
        model.addAttribute("newDonorList", rankService.getNewDonors());
        model.addAttribute("latestDonationList", rankService.getLatestDonations());
        
        
        return "rank/ranking";
    }
    
    @GetMapping("/refreshLatest")
    public String refreshLatest() {
        rankService.updateLatestDonations();
        return "redirect:/rank/top";
    }
    
    @GetMapping("/refresh")
    public String refreshRank() {
        rankService.updateRanks();
        return "redirect:/rank/top";
    }
    
    
}