package com.frontservice;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.backstage.backstagrepository.DonorRepository;
import com.entity.DonaVO;
import com.entity.RankVO;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class RankService {

    @Autowired
    private DonorRepository donorRepository;

    @PersistenceContext
    private EntityManager entityManager;  

    private List<RankVO> personalRankCache = new ArrayList<>();
    private List<RankVO> companyRankCache = new ArrayList<>();
    private List<RankVO> monthlyPersonalRankCache = new ArrayList<>();
    private List<RankVO> monthlyCompanyRankCache = new ArrayList<>();
    private List<DonaVO> newDonorCache = new ArrayList<>();
    private List<DonaVO> latestDonationList = new ArrayList<>();


    @Scheduled(cron = "0 0 * * * *")
    public void updateLatestDonations() {
        latestDonationList = donorRepository.findLatestDonations(PageRequest.of(0, 10));
    }

    public List<DonaVO> getLatestDonations() {
        return latestDonationList;
    }
    
    @Scheduled(cron = "0 0 0 * * *")
    public void updateRanks() {
        List<Object[]> personalList = donorRepository.sumPersonalDonation(PageRequest.of(0, 20));
        List<Object[]> companyList = donorRepository.sumCompanyDonation(PageRequest.of(0, 20));

        List<RankVO> personalRanks = new ArrayList<>();
        List<RankVO> companyRanks = new ArrayList<>();

        for (Object[] row : personalList) {
            String name = (String) row[0];
            Long total = (Long) row[1];
            Integer anonymous = (Integer) row[2];

            String displayName = (anonymous != null && anonymous == 1)
                ? maskName(name)
                : name;

            RankVO vo = new RankVO();
            vo.setIdentityData( displayName);
            vo.setTotalDonation(total.intValue());
            vo.setUpdatedTime(new Timestamp(System.currentTimeMillis()));
            personalRanks.add(vo);
        }

        for (Object[] row : companyList) {
            String name = (String) row[0];
            Long total = (Long) row[1];
            Integer anonymous = (Integer) row[2];

            String displayName = (anonymous != null && anonymous == 1)
                ? maskCompany(name)
                : name;

            RankVO vo = new RankVO();
            vo.setIdentityData( displayName);
            vo.setTotalDonation(total.intValue());
            vo.setUpdatedTime(new Timestamp(System.currentTimeMillis()));
            companyRanks.add(vo);
        }

        personalRankCache = personalRanks;
        companyRankCache = companyRanks;

        // 月榜
        LocalDate now = LocalDate.now();
        List<Object[]> monthlyPersonalList = donorRepository.sumMonthlyPersonalDonation(
                now.getMonthValue(), now.getYear(), PageRequest.of(0, 20)
            		);
            List<Object[]> monthlyCompanyList = donorRepository.sumMonthlyCompanyDonation(
                		now.getMonthValue(), now.getYear(), PageRequest.of(0, 20)
            );
        monthlyPersonalRankCache = monthlyPersonalList.stream().map(row -> {
            String name = (String) row[0];
            Long total = (Long) row[1];
            Integer anonymous = (Integer) row[2];
            String display = (anonymous != null && anonymous == 1) ? maskName(name) : name;

            RankVO vo = new RankVO();
            vo.setIdentityData( display);
            vo.setTotalDonation(total.intValue());
            vo.setUpdatedTime(new Timestamp(System.currentTimeMillis()));
            return vo;
        }).collect(Collectors.toList());

        monthlyCompanyRankCache = monthlyCompanyList.stream().map(row -> {
            String name = (String) row[0];
            Long total = (Long) row[1];
            Integer anonymous = (Integer) row[2];
            String display = (anonymous != null && anonymous == 1) ? maskCompany(name) : name;

            RankVO vo = new RankVO();
            vo.setIdentityData(display);
            vo.setTotalDonation(total.intValue());
            vo.setUpdatedTime(new Timestamp(System.currentTimeMillis()));
            return vo;
        }).collect(Collectors.toList());

        

       
        // 新星榜
        newDonorCache = donorRepository.findFirstTimeDonors(PageRequest.of(0, 10));
    }

    private String maskName(String name) {
        if (name == null || name.length() <= 2) return "OO";
        return name.substring(0, name.length() - 2) + "OO";
    }

    private String maskCompany(String name) {
        if (name == null || name.length() <= 2) return "OO公司";
        return name.substring(0, 2) + "OO";
    }

    public List<RankVO> getPersonalRanks() { return personalRankCache; }
    public List<RankVO> getCompanyRanks() { return companyRankCache; }
    public List<RankVO> getMonthlyPersonalRanks() { return monthlyPersonalRankCache; }
    public List<RankVO> getMonthlyCompanyRanks() { return monthlyCompanyRankCache; }
    public List<DonaVO> getNewDonors() { return newDonorCache; }

    // 手動觸發刷新用
    public void refreshNow() {
        updateRanks();
    }

    @PostConstruct  // ✅ Spring Boot 啟動時執行
    public void initLatestDonations() {
        updateLatestDonations();
        updateRanks();
    }
   
    
}
