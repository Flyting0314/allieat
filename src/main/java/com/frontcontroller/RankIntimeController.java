package com.frontcontroller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backstage.backstagrepository.DonorRepository;

@RestController
@RequestMapping("/api/donation")
public class RankIntimeController {
	
	    @Autowired
	    private DonorRepository donorRepository;

	    @GetMapping("/total-amount")
	    public Map<String, Long> getTotalDonationAmount() {
	        Long total = donorRepository.sumAllDonationIncome();
	        Map<String, Long> response = new HashMap<>();
	        response.put("totalAmount", total != null ? total : 0L);
	        return response;
	    }
	}
