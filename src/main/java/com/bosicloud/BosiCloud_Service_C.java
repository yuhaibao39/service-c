package com.bosicloud;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@EnableDiscoveryClient
@SpringBootApplication
public class BosiCloud_Service_C {

	public static void main(String[] args) {
		new SpringApplicationBuilder(BosiCloud_Service_C.class).web(true).run(args);
	}


	@Bean
	@LoadBalanced
	RestTemplate restTemplate(){
		return new RestTemplate();
	}

	@Bean
	public IRule ribbonRule() {
		return new RandomRule();//这里配置策略，和配置文件对应
	}


}
