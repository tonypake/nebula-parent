/**
 * Olymtech.com Inc.
 * Copyright (c) 2002-2015 All Rights Reserved
 */
package com.olymtech.nebula.web.exception;

import com.olymtech.nebula.entity.Callback;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author taoshanchang 15/11/20
 */
public class MyExceptionHandler implements HandlerExceptionResolver {

    protected Logger logger           = LoggerFactory.getLogger(this.getClass());

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Map<String, Object> model = new HashMap<String, Object>();

        if (handler instanceof HandlerMethod) {
            HandlerMethod method = (HandlerMethod) handler;
            logger.error("resolveException error:",ex);

            if (method.getMethod().getReturnType().getTypeName().equals("java.lang.String")){
                model.put("ex", ex);
                //根据不同错误转向不同页面
                if (ex instanceof BusinessException) {
                    return new ModelAndView("error/error-business", model);
                } else if (ex instanceof ParameterException) {
                    return new ModelAndView("error/error-parameter", model);
                } else {
                    return new ModelAndView("error/error", model);
                }
            }else{
                PrintWriter writer = null;
                try {
                    writer = response.getWriter();
                    Callback callbak = new Callback();
                    callbak.setCallbackMsg("Error");
                    callbak.setCode(500);
                    callbak.setResponseContext(ex.getMessage());

                    ObjectMapper mapper = new ObjectMapper();
                    String json = mapper.writeValueAsString(callbak);
                    writer.write(json);

                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    writer.close();
                }finally {
                    writer.close();
                }
                return null;
            }
        }
        return new ModelAndView("error/error", model);
    }

}
