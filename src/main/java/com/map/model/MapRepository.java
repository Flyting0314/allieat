package com.map.model;


import com.entity.StoreVO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MapRepository extends JpaRepository<StoreVO, Integer> {
}
