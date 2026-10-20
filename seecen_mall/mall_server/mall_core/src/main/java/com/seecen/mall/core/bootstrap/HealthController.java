package com.seecen.mall.core.bootstrap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {


    @GetMapping("/health")
    public Map<String, String> health() {

        return Map.of(
                "status", "UP",
                "message", "项目启动了",
                "service", "mall_server"
        );
    }
}
