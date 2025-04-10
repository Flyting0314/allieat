package com.frontservice;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backstage.backstagrepository.OrganizationRepository;
import com.entity.OrganizationVO;

@Service("organizationService")
public class OrganizationService {
	@Autowired
    private OrganizationRepository organizationRepository;

	
	public void addOrganization(OrganizationVO organizationVO) {
		organizationRepository.save(organizationVO);
	}

	public void updateOrganization(OrganizationVO organizationVO) {
		organizationRepository.save(organizationVO);
	}

	public void deleteOrganization(Integer organizationId) {
		if (organizationRepository.existsById(organizationId))
			organizationRepository.deleteByOrganizationId(organizationId);
//		    memberRepository.deleteById(memberId);
	}

	public OrganizationVO getOneOrganization(Integer organizationId) {
		Optional<OrganizationVO> optional = organizationRepository.findById(organizationId);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<OrganizationVO> getAll() {
		return organizationRepository.findAll();
	}
	
	
	
}
