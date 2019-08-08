package com.sixi.gateway.checksignservice;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Created with IntelliJ IDEA
 *
 * @author MiaoWoo
 */
@EnableFeignClients
@SpringCloudApplication
public class GatewayCheckSignServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayCheckSignServiceApplication.class, args);
    }
}
