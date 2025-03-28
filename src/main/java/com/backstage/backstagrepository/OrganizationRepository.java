package com.backstage.backstagrepository;




import com.entity.OrganizationVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<OrganizationVO, Integer> {
}
