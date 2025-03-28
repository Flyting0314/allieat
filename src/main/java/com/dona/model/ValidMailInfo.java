package com.dona.model;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MailInfoValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidMailInfo {
    String message() default "若需寄送收據，請填寫稱謂與身分證號或統一編號";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

