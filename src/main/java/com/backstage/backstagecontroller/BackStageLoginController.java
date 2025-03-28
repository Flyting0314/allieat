package com.backstage.backstagecontroller;




import com.backstage.backstageservice.BackStageLoginService;
import com.entity.AdminVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/backStage")
@CrossOrigin(origins = "*")
public class BackStageLoginController {


    @Autowired
    private BackStageLoginService backStageLoginService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AdminVO admin) {
        return backStageLoginService.findByAccount(admin);
    }
}

