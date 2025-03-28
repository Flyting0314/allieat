package com.dona.model;
import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DonaRepository extends JpaRepository<Dona, Integer> {
    // 如果需要自定義查詢，這裡可以加入 JPQL 或 Native Query
	@Query("SELECT d FROM Dona d WHERE "
	         + "(:salutation IS NULL OR d.salutation = :salutation) AND "
	         + "(:idNum IS NULL OR d.idNum = :idNum) AND "
	         + "(:guiNum IS NULL OR d.guiNum = :guiNum) AND "
	         + "(:startTime IS NULL OR d.createdTime >= :startTime) AND "
	         + "(:endTime IS NULL OR d.createdTime <= :endTime)")
	    List<Dona> findByCriteria(@Param("salutation") String salutation,
	                              @Param("idNum") String idNum,
	                              @Param("guiNum") String guiNum,
	                              @Param("startTime") Timestamp startTime,
	                              @Param("endTime") Timestamp endTime);
	
	
	
	
	@Query("SELECT d FROM Dona d WHERE "
		     + "(:salutation IS NOT NULL AND d.salutation = :salutation) AND "
		     + "((:idNum IS NOT NULL AND d.idNum = :idNum) OR (:guiNum IS NOT NULL AND d.guiNum = :guiNum))")
		List<Dona> findBySalutationAndEitherIdNumOrGuiNum(@Param("salutation") String salutation,
		                                                  @Param("idNum") String idNum,
		                                                  @Param("guiNum") String guiNum);
	
	
	@Query("SELECT d FROM Dona d WHERE " +
		       "(:salutation IS NULL OR d.salutation = :salutation) AND " +
		       "(:idNum IS NOT NULL AND d.idNum = :idNum OR " +
		       " :guiNum IS NOT NULL AND d.guiNum = :guiNum) AND " +
		       "(:startTime IS NULL OR d.createdTime >= :startTime) AND " +
		       "(:endTime IS NULL OR d.createdTime <= :endTime)")
		List<Dona> findBySalutationAndIdOrGui(@Param("salutation") String salutation,
		                                      @Param("idNum") String idNum,
		                                      @Param("guiNum") String guiNum,
		                                      @Param("startTime") Timestamp startTime,
		                                      @Param("endTime") Timestamp endTime);
	
}
