package com.donation.model;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backstage.backstagrepository.DonationRepository;
import com.entity.DonationVO;

//import hibernate.util.CompositeQuery.HibernateUtil_CompositeQuery_Donation;


@Service("donationService")
public class DonationService {

	@Autowired
	DonationRepository repository;
	
	@Autowired
    private SessionFactory sessionFactory;

	public void addDonation(DonationVO donationVO) {
		repository.save(donationVO);
	}

	public void updateDonation(DonationVO donationVO) {
		repository.save(donationVO);
	}

	public void deleteDonation(Integer rankId) {
		if (repository.existsById(rankId))
			repository.deleteByRankId(rankId);
//		    repository.deleteById(rankId);
	}

	public DonationVO getOneDonation(Integer rankId) {
		Optional<DonationVO> optional = repository.findById(rankId);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	//原本
//	public List<DonationVO> getAll() {
//		return repository.findAll();
//	}
	
	public List<DonationVO> getAllDonation() {
		return repository.findAll();
	}


//	public List<DonationVO> getAll(Map<String, String[]> map) {
//		return HibernateUtil_CompositeQuery_Donation.getAllC(map,sessionFactory.openSession());
//	}

}