package com.backstage.backstageinterceptor;


import com.jwtUtil.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import java.io.IOException;

/*
* Jwt攔截器，根據登入有沒有JwtToken來決定是否發送回覆。
* 適用範圍，路徑具有/backStage的控制器，其中排除/backStage/login。
*/
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;
  

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String path = request.getRequestURI();

        // 只攔截 /backStage/**，這邊可以省略判斷，交由 WebConfig 控制攔截範圍

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return false;
        }
        //取出前端發送的token
        String token = authHeader.replaceFirst("^Bearer\\s+", "");
        //非法token或過期token處理。
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired JWT token");
            return false;
        }

        return true;
    }
}
