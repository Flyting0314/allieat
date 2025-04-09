package com.frontservice;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backstage.backstagrepository.PhotoRepository;
import com.entity.PhotoVO;



@Service("photoService")
public class PhotoService {

	@Autowired
	PhotoRepository photoRepository;
	
	public void addPhoto(PhotoVO photoVO) {
		photoRepository.save(photoVO);
	}

	public void updatePhoto(PhotoVO photoVO) {
		photoRepository.save(photoVO);
	}


	public PhotoVO getOnePhoto(Integer photoId) {
		Optional<PhotoVO> optional = photoRepository.findById(photoId);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<PhotoVO> getAll() {
		return photoRepository.findAll();
	}
	public List<PhotoVO> getPhotosByStoreId(Integer storeId) {
	    return photoRepository.findByStoreStoreId(storeId);
	}

}