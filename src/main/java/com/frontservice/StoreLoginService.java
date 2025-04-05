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

        // 安全性：只允許更新自己
        Optional<StoreVO> storeOpt = storeRepository.findById(sessionStore.getStoreId());
        if (storeOpt.isEmpty()) return false;

        StoreVO store = storeOpt.get();

        // 驗證格式與空值處理
        if (formInput.getOpTime() == null || formInput.getLastOrder() == null ||
            formInput.getCloseTime() == null || formInput.getPickTime() == null) {
            return false;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        
        // 資料更新
//        store.setOpTime(formInput.getOpTime());
//        store.setLastOrder(formInput.getLastOrder());
//        store.setCloseTime(formInput.getCloseTime());
//        store.setPickTime(formInput.getPickTime());

        try {
            LocalTime op = LocalTime.parse(formInput.getOpTime(), formatter);
            LocalTime last = LocalTime.parse(formInput.getLastOrder(), formatter);
            LocalTime close = LocalTime.parse(formInput.getCloseTime(), formatter);
            if (op.isAfter(close) || last.isBefore(op) || last.isAfter(close)) {
                System.out.println("時間邏輯錯誤：opTime 必須 < closeTime，lastOrder 需在兩者之間");
                return false;
            }
            store.setOpTime(op.format(formatter));
            store.setLastOrder(last.format(formatter));
            store.setCloseTime(close.format(formatter));
        	
            storeRepository.save(store);
            System.out.println("準備更新商家資料: " + formInput.getLastOrder());
            System.out.println("接收到的資料：");
            System.out.println("opTime: " + formInput.getOpTime());
            System.out.println("lastOrder: " + formInput.getLastOrder());
            System.out.println("closeTime: " + formInput.getCloseTime());
            System.out.println("pickTime: " + formInput.getPickTime());
            return true;
        } catch (Exception e) {
            e.printStackTrace(); // 或記錄 log
            return false;
        }
    }
    
}