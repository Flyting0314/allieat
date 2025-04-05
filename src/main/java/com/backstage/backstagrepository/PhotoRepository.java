package com.backstage.backstagrepository;




import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.entity.PhotoVO;
import com.entity.StoreVO;

@Repository
public interface PhotoRepository extends JpaRepository<PhotoVO, Integer> {
	List<PhotoVO> findByStoreOrderByUpdateTimeAsc(StoreVO store);
	List<PhotoVO> findByStoreOrderByPhotoIdAsc(StoreVO store);
	Optional<PhotoVO> findTopByStoreAndPhotoType(StoreVO store, String photoType);
}
