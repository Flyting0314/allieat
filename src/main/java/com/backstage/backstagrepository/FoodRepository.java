package com.backstage.backstagrepository;



import com.entity.FoodVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository extends JpaRepository<FoodVO, Integer> {
}
