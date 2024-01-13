package com.bosicloud.web;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import com.bosicloud.entity.InterfaceLimit;
import com.bosicloud.service.InterfaceLimitService;

import org.springframework.web.client.RestTemplate;
import redis.clients.jedis.Jedis;

@RestController
public class ComputeController {

    private final Logger logger = Logger.getLogger(getClass());

    @Autowired
    private InterfaceLimitService service;

    //单位时间内的调用次数
    private final int flag = 10;
    //单位时间1000ms * 60 = 1min
    private static final int timeRound = 1000 * 60;
    //用来标记调用次数
    private static final AtomicLong num = new AtomicLong(0);

    @Autowired
    private DiscoveryClient client;


    @Autowired
    RestTemplate restTemplate;//定义为私有可能会报错


    static {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                num.set(0);
            }
        }, 0, timeRound);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String add001(@RequestParam Integer a, @RequestParam Integer b) {

        ServiceInstance instance = client.getLocalServiceInstance();
        Integer r = a + b;

        logger.info("/add, host:" + instance.getHost() + ", service_id:" + instance.getServiceId() + ", result:" + r);

        return "计算结果 From Service-B, Result is " + r + "\nPort:" + instance.getPort();

    }

    //测试skywalking服务
    @RequestMapping(value = "/skywalking", method = RequestMethod.GET)
    public String addauthorizationok(@RequestHeader Map<String, String> headerMap, @RequestParam Integer a, @RequestParam Integer b) {

        logger.info("love C print HeaderMap:" + headerMap);

        ServiceInstance instance = client.getLocalServiceInstance();
        Integer r = a + b;

        logger.info("love Service-C, /love, host:" + instance.getHost() + ", service_id:" + instance.getServiceId() + ", result:" + r);

        //远程服务调用测试
        return restTemplate.getForObject("http://service-D:9998/skywalking?a=" + a + "&b=" + b, String.class);

    }


}