package com.map.model;

import com.entity.StoreVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MapServiceImpl implements MapService {

    @Autowired
    private MapRepository mapRepository;

    @Override
    public List<StoreVO> findAll() {
        return mapRepository.findAll();
    }
}
