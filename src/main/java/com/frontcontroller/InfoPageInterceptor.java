package com.frontcontroller;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

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

        if (isStoreLoggedIn || isMemberLoggedIn) {
         
            return true;
        }

      
        response.sendRedirect(request.getContextPath() + "/registerAndLogin/login");
        
        response.sendRedirect(request.getContextPath() + "/registerAndLogin/login?from=map2");
        
        return false;
    }
	
}
