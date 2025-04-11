package com.backstage.backstagecontroller;




import com.backstage.backstagedto.AdminDTO;
import com.backstage.backstageservice.BackStageLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/backStage")
public class BackStageLoginController {

    @Autowired
    private BackStageLoginService backStageLoginService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody AdminDTO admin) {
        return backStageLoginService.findByAccount(admin);
    }
}

