package com.sixi.gateway.checksignservice.oauth.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: ZY
 * @Date: 2019/8/7 9:15
 * @Version 1.0
 * @Description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthConsumer implements Serializable {

    private static final long serialVersionUID = -719911794290149110L;

    private String key;

    private String secret;

}
