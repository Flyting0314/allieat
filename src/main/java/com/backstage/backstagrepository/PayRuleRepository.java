package com.backstage.backstagrepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.entity.PayRuleVO;

@Repository
public interface PayRuleRepository extends JpaRepository<PayRuleVO, Integer> {

}
