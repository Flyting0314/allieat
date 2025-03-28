package com.backstage.backstagrepository;



import com.entity.FoodTypeVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodTypeRepository extends JpaRepository<FoodTypeVO, Integer> {
}
