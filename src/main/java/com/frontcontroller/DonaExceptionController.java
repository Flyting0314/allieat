package com.frontcontroller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
@ControllerAdvice
public class DonaExceptionController {

    private static final Logger logger = LoggerFactory.getLogger(DonaExceptionController.class);

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(HttpServletRequest req, Exception ex) throws Exception {
        logger.error("Request URL : {}, Exception : {}", req.getRequestURL(), ex.getMessage(), ex);

        // 如果有 @ResponseStatus，讓 Spring 處理（例如 404、500）
        if (AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class) != null) {
            throw ex;
        }

        ModelAndView mav = new ModelAndView();
        mav.addObject("url", req.getRequestURL());
        mav.addObject("errorMessage", ex.getMessage()); // 只傳簡單的 message 字串
        mav.setViewName("error/error");

        return mav;
    }
    
}