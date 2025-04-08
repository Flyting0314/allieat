package com.store.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backstage.backstagrepository.StoreRepository;
import com.entity.StoreVO;

import java.util.List;

@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    // 取得指定 ID 的餐廳
    public StoreVO getStoreById(Integer storeId) {
        return storeRepository.findById(storeId).orElse(null);
    }

    // 取得所有餐廳
    public List<StoreVO> getAllStores() {
        return storeRepository.findAll();
    }
}
