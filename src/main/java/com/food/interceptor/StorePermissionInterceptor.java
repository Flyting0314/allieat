package com.food.interceptor;

import com.entity.StoreVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StorePermissionInterceptor implements HandlerInterceptor {

    private static final Pattern STORE_ID_PATTERN = Pattern.compile("/registerAndLogin/storeInfo/(\\d+)/food");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 從 URL 中擷取 storeId
        String uri = request.getRequestURI();
        Matcher matcher = STORE_ID_PATTERN.matcher(uri);

        if (matcher.find()) {
            Integer pathStoreId = Integer.parseInt(matcher.group(1));

            // 從 session 中取得登入的 StoreVO
            StoreVO loginStore = (StoreVO) request.getSession().getAttribute("store");

            if (loginStore == null) {
                System.out.println("未登入，導向登入頁面");
                response.sendRedirect(request.getContextPath() + "/registerAndLogin/login");
                return false;
            }

            if (!loginStore.getStoreId().equals(pathStoreId)) {
                System.out.println("權限不足：session storeId = " + loginStore.getStoreId() + "，路徑 storeId = " + pathStoreId);
                response.sendRedirect(request.getContextPath() + "/registerAndLogin/login"); // 或導向自訂的權限錯誤頁
                return false;
            }

            // ✅ 驗證成功，放行
            return true;
        }

        // 若非要攔的路徑格式，直接放行
        return true;
    }
}
