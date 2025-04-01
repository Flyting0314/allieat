package com.backstage.backstagrepository;



import com.entity.RankVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RankRepository extends JpaRepository<RankVO, Integer> {
}
