package com.backstage.backstagrepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.entity.AttachedVO;

@Repository
public interface AttachedRepository extends JpaRepository<AttachedVO, Integer> {
}
