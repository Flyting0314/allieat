package com.backstage.backstagrepository;


import com.entity.DonaVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DonorRepository extends JpaRepository<DonaVO, Integer> {
    @Query(value = "SELECT COUNT(*) FROM donationrecord WHERE createdTime BETWEEN DATE_SUB(NOW(), INTERVAL 1 MONTH) AND NOW()", nativeQuery = true)
    long countDonorLastMonth();

}
