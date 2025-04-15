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
		HttpSession session = request.getSession();
	    session.setAttribute("redirectFromProtected", true);
	    response.sendRedirect("/registerAndLogin/login");
	    return false;
    }
	
}
