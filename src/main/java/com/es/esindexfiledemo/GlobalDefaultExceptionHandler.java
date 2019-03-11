package com.es.esindexfiledemo;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalDefaultExceptionHandler {
    private Logger logger = LoggerFactory.getLogger(GlobalDefaultExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    public void defaultErrorHandler(HttpServletRequest req, Exception e, HttpServletResponse response) throws IOException {
        logger.error(e.getMessage(), e);

        Map<String, String> map = new HashMap<>();
        map.put("msg", e.getMessage());

        Writer writer = response.getWriter();
        writer.write(JSON.toJSONString(map));
    }

}
