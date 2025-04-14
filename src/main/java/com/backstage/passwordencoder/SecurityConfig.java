    package com.backstage.passwordencoder;

    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    /*
     * BCryptPassword設定，應用於後台註冊/登入密碼加密應用。
     */
    @Configuration
    public class SecurityConfig     {
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
    }
