package com.sixi.gateway.checksignservice.config;

import com.sixi.gateway.checksignservice.filter.CustomGlobalFilter;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @Author: ZY
 * @Date: 2019/8/5 13:59
 * @Version 1.0
 * @Description: 过滤器配置
 */

@SpringBootConfiguration
public class FilterConfig {

    @Bean
    public CustomGlobalFilter authSignatureFilter() {
        return new CustomGlobalFilter();
    }
}
