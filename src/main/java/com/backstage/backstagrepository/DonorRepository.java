package com.backstage.backstagrepository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import com.entity.DonaVO;

@Repository
public interface DonorRepository extends JpaRepository<DonaVO, Integer> {
    @Query(value = "SELECT COUNT(*) FROM donationrecord WHERE createdTime BETWEEN DATE_SUB(NOW(), INTERVAL 1 MONTH) AND NOW()", nativeQuery = true)
    long countDonorLastMonth();
    
 // 如果需要自定義查詢，這裡可以加入 JPQL 或 Native Query
 	@Query("SELECT d FROM DonaVO d WHERE "
 	         + "(:salutation IS NULL OR d.salutation = :salutation) AND "
 	         + "(:idNum IS NULL OR d.idNum = :idNum) AND "
 	         + "(:guiNum IS NULL OR d.guiNum = :guiNum) AND "
 	         + "(:startTime IS NULL OR d.createdTime >= :startTime) AND "
 	         + "(:endTime IS NULL OR d.createdTime <= :endTime)")
 	    List<DonaVO> findByCriteria(@Param("salutation") String salutation,
 	                              @Param("idNum") String idNum,
 	                              @Param("guiNum") String guiNum,
 	                              @Param("startTime") Timestamp startTime,
 	                              @Param("endTime") Timestamp endTime);
 	
 	
 	
 	
 	@Query("SELECT d FROM DonaVO d WHERE "
 		     + "(:salutation IS NOT NULL AND d.salutation = :salutation) AND "
 		     + "((:idNum IS NOT NULL AND d.idNum = :idNum) OR (:guiNum IS NOT NULL AND d.guiNum = :guiNum))")
 		List<DonaVO> findBySalutationAndEitherIdNumOrGuiNum(@Param("salutation") String salutation,
 		                                                  @Param("idNum") String idNum,
 		                                                  @Param("guiNum") String guiNum);
 	
 	
 	@Query("SELECT d FROM DonaVO d WHERE " +
 		       "(:salutation IS NULL OR d.salutation = :salutation) AND " +
 		       "(:idNum IS NOT NULL AND d.idNum = :idNum OR " +
 		       " :guiNum IS NOT NULL AND d.guiNum = :guiNum) AND " +
 		       "(:startTime IS NULL OR d.createdTime >= :startTime) AND " +
 		       "(:endTime IS NULL OR d.createdTime <= :endTime)")
 		List<DonaVO> findBySalutationAndIdOrGui(@Param("salutation") String salutation,
 		                                      @Param("idNum") String idNum,
 		                                      @Param("guiNum") String guiNum,
 		                                      @Param("startTime") Timestamp startTime,
 		                                      @Param("endTime") Timestamp endTime);
 	
 	
//=========================捐款排行用
 	@Query("""
 	        SELECT d.identityData, SUM(d.donationIncome), d.anonymous
 	        FROM DonaVO d
 	        WHERE d.guiNum IS NULL
 	        GROUP BY d.identityData, d.email, d.phone, d.anonymous
 	        ORDER BY SUM(d.donationIncome) DESC
 	    """)
 	    List<Object[]> sumPersonalDonation(Pageable pageable);

 	    @Query("""
 	        SELECT d.identityData, SUM(d.donationIncome), d.anonymous
 	        FROM DonaVO d
 	        WHERE d.guiNum IS NOT NULL
 	        GROUP BY d.identityData, d.guiNum, d.anonymous
 	        ORDER BY SUM(d.donationIncome) DESC
 	    """)
 	    List<Object[]> sumCompanyDonation(Pageable pageable);

 	   @Query("""
 			    SELECT d.identityData, SUM(d.donationIncome), d.anonymous
 			    FROM DonaVO d
 			    WHERE MONTH(d.createdTime) = :month AND YEAR(d.createdTime) = :year AND d.guiNum IS NULL
 			    GROUP BY d.identityData, d.anonymous
 			    ORDER BY SUM(d.donationIncome) DESC
 			""")
 			List<Object[]> sumMonthlyPersonalDonation(@Param("month") int month, @Param("year") int year, Pageable pageable);


 			@Query("""
 			    SELECT d.identityData, SUM(d.donationIncome), d.anonymous
 			    FROM DonaVO d
 			    WHERE MONTH(d.createdTime) = :month AND YEAR(d.createdTime) = :year AND d.guiNum IS NOT NULL
 			    GROUP BY d.identityData, d.anonymous
 			    ORDER BY SUM(d.donationIncome) DESC
 			""")
 			List<Object[]> sumMonthlyCompanyDonation(@Param("month") int month, @Param("year") int year, Pageable pageable);

 	    @Query("""
 	        SELECT d FROM DonaVO d
 	        WHERE NOT EXISTS (
 	            SELECT 1 FROM DonaVO x
 	            WHERE x.email = d.email AND x.phone = d.phone
 	            AND x.createdTime < d.createdTime
 	        )
 	        ORDER BY d.createdTime DESC
 	    """)
 	    List<DonaVO> findFirstTimeDonors(Pageable pageable);

 	    @Query("SELECT d FROM DonaVO d ORDER BY d.createdTime DESC")
 	    List<DonaVO> findLatestDonations(Pageable pageable);
 	    
 	    
 	   @Query("SELECT SUM(d.donationIncome) FROM DonaVO d")
 	  Long sumAllDonationIncome();
//======================捐款查詢用
 	  @Query("""
 		        SELECT d FROM DonaVO d 
 		        WHERE d.phone = :phone AND d.email = :email
 		        ORDER BY d.createdTime DESC
 		    """)
 		    List<DonaVO> findByEmailAndPhone(@Param("email") String email,
 		                                     @Param("phone") String phone);
 	  
 	  
 	 @Query("""
 		    SELECT d FROM DonaVO d 
 		    WHERE d.phone = :phone AND d.email = :email
 		    AND (:startTime IS NULL OR d.createdTime >= :startTime) 
 		    AND (:endTime IS NULL OR d.createdTime <= :endTime)
 		    ORDER BY d.createdTime DESC
 		""")
 		List<DonaVO> findByEmailAndPhoneWithOptionalDate(@Param("email") String email,
 		                                                 @Param("phone") String phone,
 		                                                 @Param("startTime") Timestamp startTime,
 		                                                 @Param("endTime") Timestamp endTime);

//====================== 	   
 	   
 	@Transactional
 	@Modifying
 	@Query("UPDATE DonaVO d SET d.paidStatus = 1 WHERE d.merchantTradeNo = :merchantTradeNo")
 	int updatePaidStatusByMerchantTradeNo(@Param("merchantTradeNo") String merchantTradeNo);
   
 	}

