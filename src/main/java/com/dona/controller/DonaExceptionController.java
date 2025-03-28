package com.dona.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
@ControllerAdvice
public class DonaExceptionController {
	private final Logger logger = LoggerFactory.getLogger(DonaController.class);
	
	@ExceptionHandler(Exception.class)
	public ModelAndView handleException(HttpServletRequest req,Exception e) throws Exception{
		logger.error("Request URL : {} , Exception : {} ",req.getRequestURL(),e.getMessage());
		
		if(AnnotationUtils.findAnnotation(e.getClass(),ResponseStatus.class)!=null) {
			throw e;
		}
		ModelAndView mav = new ModelAndView();
		mav.addObject("url",req.getRequestURL());
		mav.addObject("exception",e);
		mav.setViewName("error/error");
		
		return mav;
		
	}
}


