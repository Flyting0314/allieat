package com.backstage.backstagrepository;




import com.entity.PhotoVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends JpaRepository<PhotoVO, Integer> {
}
