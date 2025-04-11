package com.backstage.backstageservice;



import com.backstage.backstagedto.AdminDTO;
import org.springframework.http.ResponseEntity;
import java.util.Map;

public interface BackStageLoginService {

    ResponseEntity<Map<String, Object>> findByAccount(AdminDTO admin);
}
