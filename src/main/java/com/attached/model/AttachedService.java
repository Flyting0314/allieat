package com.attached.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backstage.backstagrepository.AttachedRepository;
import com.entity.AttachedVO;

import java.util.List;
import java.util.Optional;

@Service
public class AttachedService {

    @Autowired
    private AttachedRepository attachedRepository;

    public List<AttachedVO> getAllAttached() {
        return attachedRepository.findAll();
    }

    public Optional<AttachedVO> getAttachedById(Integer attachedId) {
        return attachedRepository.findById(attachedId);
    }

    public List<AttachedVO> getAttachedByFoodId(Integer foodId) {
        return attachedRepository.findByFood_FoodId(foodId);
    }

    public AttachedVO saveAttached(AttachedVO attachedVO) {
        return attachedRepository.save(attachedVO);
    }

    public void deleteAttached(Integer attachedId) {
        attachedRepository.deleteById(attachedId);
    }
}
