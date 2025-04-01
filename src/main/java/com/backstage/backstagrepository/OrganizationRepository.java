package com.backstage.backstagrepository;




import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.entity.OrganizationVO;

@Repository
public interface OrganizationRepository extends JpaRepository<OrganizationVO, Integer> {
	@Transactional
	@Modifying
	@Query(value = "delete from organization where organizationId =?1", nativeQuery = true)
	void deleteByOrganizationId(int organizationId);

	//● (自訂)條件查詢
	@Query(value = "from OrganizationVO where organizationId=?1 and name like?2 and createdTime=?3 order by organizationId")
	List<OrganizationVO> findByOthers(int organizationId , String name , Timestamp createdTime);
	Optional<OrganizationVO> findByName(String name);
}
