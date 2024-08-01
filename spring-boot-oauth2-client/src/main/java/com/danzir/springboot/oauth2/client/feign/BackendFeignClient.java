package com.danzir.springboot.oauth2.client.feign;

import com.danzir.springboot.oauth2.client.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@FeignClient(value = "backend", url = "${resource-server.base.url}", configuration = FeignConfig.class)
public interface BackendFeignClient {

    @RequestMapping(method = RequestMethod.GET, value = "/v1/backend/permissions/check")
    Map getPermissions(@RequestHeader("jwt-token") String accessToken);

}
