package com.car.model;

import com.entity.AttachedVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CarAttachedRepository extends JpaRepository<AttachedVO, Integer> {

    @Query("SELECT a FROM AttachedVO a JOIN FETCH a.food")
    List<AttachedVO> findAllWithFood();
    
    
}
