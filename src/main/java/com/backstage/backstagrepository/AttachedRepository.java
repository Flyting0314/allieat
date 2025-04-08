package com.backstage.backstagrepository;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import com.entity.AttachedVO;

import java.util.List;

@Repository
public interface AttachedRepository extends ListCrudRepository<AttachedVO, Integer> {
    List<AttachedVO> findByFood_FoodId(Integer foodId);
}
