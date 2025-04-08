package com.car.model;

import com.entity.FoodVO;
import com.entity.AttachedVO;
import java.util.List;

public class OrderWrapper {
    private List<FoodVO> mainDishes;
    private List<AttachedVO> attachedItems;

    public OrderWrapper(List<FoodVO> mainDishes, List<AttachedVO> attachedItems) {
        this.mainDishes = mainDishes;
        this.attachedItems = attachedItems;
    }

    public List<FoodVO> getMainDishes() {
        return mainDishes;
    }

    public void setMainDishes(List<FoodVO> mainDishes) {
        this.mainDishes = mainDishes;
    }

    public List<AttachedVO> getAttachedItems() {
        return attachedItems;
    }

    public void setAttachedItems(List<AttachedVO> attachedItems) {
        this.attachedItems = attachedItems;
    }
}
