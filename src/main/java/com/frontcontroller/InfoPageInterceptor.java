package com.frontcontroller;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.entity.MemberVO;
import com.entity.StoreVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class InfoPageInterceptor implements HandlerInterceptor {

	
        
       
	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false); 

        boolean isStoreLoggedIn = session != null && session.getAttribute("store") != null;
        boolean isMemberLoggedIn = session != null && session.getAttribute("member") != null;

        if (isStoreLoggedIn) {
            StoreVO store = (StoreVO) session.getAttribute("store");
            if (store != null && store.getStoreId() != null) {
                return true;
            }
        }

        if (isMemberLoggedIn) {
            MemberVO member = (MemberVO) session.getAttribute("member");
            if (member != null && member.getMemberId() != null) {
                return true;
            }
        }

      
        response.sendRedirect(request.getContextPath() + "/registerAndLogin/login");
        
        response.sendRedirect(request.getContextPath() + "/registerAndLogin/login?from=map2");
        
        return false;
    }
	
}
