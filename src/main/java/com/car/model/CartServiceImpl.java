package com.car.model;

import com.entity.AttachedVO;
import com.entity.FoodVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CarFoodRepository foodRepository;

    @Autowired
    private CarAttachedRepository attachedRepository;

    @Override
    public List<FoodVO> getMainDishes() {
        return foodRepository.findAll();
    }

    @Override
    public List<AttachedVO> getAttachedItems() {
        return attachedRepository.findAllWithFood();
    }

    @Override
    public Optional<FoodVO> getMainDishById(Integer foodId) {
        return foodRepository.findById(foodId);
    }

    @Override
    public Optional<AttachedVO> getAttachedItemById(Integer attachedId) {
        return attachedRepository.findById(attachedId);
    }
}
