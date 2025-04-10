package com.backstage.backstagecontroller;


import com.backstage.backstagedto.ChangePasswordDTO;
import com.backstage.backstagedto.NewAdminDTO;
import com.backstage.backstageservice.BackStageNewAdminServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/backStage")
public class BackStageNewAdminController {
    @Autowired
    private BackStageNewAdminServiceImpl backStageNewAdminServiceImpl;
    @PostMapping("/admin")
    public ResponseEntity<NewAdminDTO > newAdmin(@RequestBody @Valid NewAdminDTO newAdmin) {
        System.out.println(newAdmin.getAccount());
        System.out.println(newAdmin.getPassword());
        return backStageNewAdminServiceImpl.newAdmin(newAdmin);
    }
    @PutMapping("/admin/changePassword")
    public ResponseEntity<ChangePasswordDTO > changePassword(@RequestBody @Valid ChangePasswordDTO Admin) {
        return backStageNewAdminServiceImpl.changePassword(Admin);
    }

    //以下為驗證錯誤處理，僅限本控制器使用，處理項目為MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "fail");
        List<FieldError> errors = ex.getBindingResult().getFieldErrors();
        String message = "驗證失敗";

        if (!errors.isEmpty()) {
            FieldError firstError = errors.get(0);
            message = firstError.getDefaultMessage();
        }
        result.put("message", message);
        return ResponseEntity.badRequest().body(result);
    }
}
