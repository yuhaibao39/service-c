package com.bosicloud.web;

import com.alibaba.fastjson.*;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/*
 *@name  springboot AOP切面监控接口调用详情
 *@author:  steffens
 *@create   2020-05-19
 *
 **/
@Aspect
@Component
public class SectionMonitor {

    private static final ThreadLocal<Long> timeTreadLocal = new ThreadLocal<>();

    @Pointcut("execution(public * com.bosicloud.web.*.*(..))")
    public void log() {
    }

    @Before("log()")
    public void before(JoinPoint joinPoint) {

        Object[] inputArgs = joinPoint.getArgs();
        for (Object arg : inputArgs) {
            System.out.println("*******************arg：" + arg);
/*            if (arg.getClass().equals("HttpPost")) {
                final HttpPost request = (HttpPost) arg;
                request.removeHeaders("sw8");
                request.removeHeaders("sw8-correlation");
                request.removeHeaders("sw8-x");
                System.out.println(request.getAllHeaders());
            }*/
        }
        System.out.println("deAfter");


        timeTreadLocal.set(System.currentTimeMillis());
        // 接收到请求，记录请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //获取请求的request
        HttpServletRequest request = attributes.getRequest();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        //获取被拦截的方法
        Method method = methodSignature.getMethod();
        //获取被拦截的方法名
        String methodName = method.getName();
        System.out.println("接口方法名称：" + methodName + "()");
        //获取所有请求参数key和value
        String keyValue = getReqParameter(request);
        System.out.println("url = " + request.getRequestURL().toString());
        System.out.println("方法类型 = " + request.getMethod());
        System.out.println("请求参数 key：value = " + keyValue);
    }


    @After("log()")
    public void after() {
        System.out.println("aop的after()方法");
    }

    //controller请求结束返回时调用
    @AfterReturning(returning = "result", pointcut = "log()")
    public Object afterReturn(Object result) {
        System.out.println("返回值result =" + result.toString());
        long startTime = timeTreadLocal.get();
        double callTime = (System.currentTimeMillis() - startTime) / 1000.0;
        System.out.println("调用接口共花费时间time = " + callTime + " s");
        return result;
    }

    /**
     * 获取所有请求参数，封装为map对象
     *
     * @return
     */
    public Map<String, Object> getParameterMap(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Enumeration<String> enumeration = request.getParameterNames();
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        StringBuilder stringBuilder = new StringBuilder();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            String value = request.getParameter(key);
            String keyValue = key + " : " + value + " ; ";
            stringBuilder.append(keyValue);
            parameterMap.put(key, value);
        }
        return parameterMap;
    }

    public String getReqParameter(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Enumeration<String> enumeration = request.getParameterNames();
        //StringBuilder stringBuilder = new StringBuilder();
        JSONArray jsonArray = new JSONArray();
        while (enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            String value = request.getParameter(key);
            JSONObject json = new JSONObject();
            json.put(key, value);
            jsonArray.add(json);
        }
        return jsonArray.toString();
    }
}
