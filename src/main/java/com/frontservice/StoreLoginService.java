package com.frontservice;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backstage.backstagrepository.StoreRepository;
import com.entity.StoreVO;

@Service
public class StoreLoginService {

    @Autowired
    private StoreRepository storeRepository;

    public StoreVO login(String email, String password) {
        return storeRepository.findByEmailAndPassword(email, password);
    }
    @Transactional
    public boolean updateStoreEditableFields(StoreVO sessionStore, StoreVO formInput) {
        if (sessionStore == null || formInput == null) return false;

        Optional<StoreVO> storeOpt = storeRepository.findById(sessionStore.getStoreId());
        if (storeOpt.isEmpty()) return false;

        StoreVO store = storeOpt.get();

        // 驗證欄位是否為空
        if (formInput.getOpTime() == null || formInput.getLastOrder() == null || 
            formInput.getCloseTime() == null || formInput.getPickTime() == null) {
            return false;
        }

        // 驗證時間邏輯
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalTime op = LocalTime.parse(formInput.getOpTime(), formatter);
        LocalTime close = LocalTime.parse(formInput.getCloseTime(), formatter);
        LocalTime last = LocalTime.parse(formInput.getLastOrder(), formatter);

        if (!op.isBefore(close)) return false;
        if (last.isBefore(op) || last.isAfter(close)) return false;


        sessionStore.setOpTime(formInput.getOpTime());
        sessionStore.setCloseTime(formInput.getCloseTime());
        sessionStore.setLastOrder(formInput.getLastOrder());
        sessionStore.setPickTime(formInput.getPickTime());
       

        storeRepository.save(sessionStore);
        return true;
    }


    public boolean isStoreMissingOpeningInfo(StoreVO store) {
        return isBlank(store.getOpTime()) ||
               isBlank(store.getCloseTime()) ||
               isBlank(store.getLastOrder()) ||
               isBlank(store.getPickTime());
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
    public boolean isInvalidTimeOrder(StoreVO store) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime op = LocalTime.parse(store.getOpTime(), formatter);
            LocalTime close = LocalTime.parse(store.getCloseTime(), formatter);
            LocalTime last = LocalTime.parse(store.getLastOrder(), formatter);

            if (!op.isBefore(close)) return true; // 開始時間要早於結束
            if (last.isBefore(op) || last.isAfter(close)) return true; // 最後點餐必須在營業區間內
        } catch (Exception e) {
            return true; // 有格式錯誤也當作無效
        }
        return false;
    }
    
}