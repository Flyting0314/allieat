package com.backstage.backstageservice;



import com.entity.AdminVO;
import org.springframework.http.ResponseEntity;
import java.util.Map;

public interface BackStageLoginService {
    ResponseEntity<Map<String, Object>> findByAccount(AdminVO admin);
}
