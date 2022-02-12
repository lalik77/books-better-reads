package com.mami.betterreads.exception;


import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BetterReadsExceptionHandler {

  private static final String LOGO = "/images/logo.png";

  private Logger logger=LoggerFactory.getLogger(BetterReadsExceptionHandler.class);

  @ExceptionHandler(value = BetterReadsException.class)
  public String parseHandler(BetterReadsException ex,HttpServletRequest request, Model model) {

    model.addAttribute("logo",LOGO);

    logger.error("Request  : " + request.getRequestURL() + " ====> " + ex);

    return  "error";
  }

  @ExceptionHandler(value = RuntimeException.class)
  public String submissionHandler(RuntimeException ex,HttpServletRequest request, Model model) {

    model.addAttribute("logo",LOGO);

    logger.error("Request  : " + request.getRequestURL() + " ====> " + ex);

    return  "error";
  }

}
