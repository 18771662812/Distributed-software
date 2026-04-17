package org.example.shop.Controller;

import org.example.shop.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Nacos / Gateway 联调测试控制器
 */
@RefreshScope
@RestController
@RequestMapping
public class SystemController {

    private final Environment environment;

    @Value("${shop.dynamic-message}")
    private String dynamicMessage;

    public SystemController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping("/system/instance")
    public Result<Map<String, String>> getInstanceInfo() {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("serviceName", environment.getProperty("spring.application.name", "unknown-service"));
        data.put("instanceName", environment.getProperty("INSTANCE_NAME", "unknown-instance"));
        data.put("port", environment.getProperty("local.server.port", environment.getProperty("server.port", "unknown-port")));
        data.put("hostname", environment.getProperty("HOSTNAME", "unknown-host"));
        return Result.success(data);
    }

    @GetMapping("/config/runtime")
    public Result<Map<String, String>> getDynamicConfig() {
        Map<String, String> data = new LinkedHashMap<>();
        data.put("dynamicMessage", dynamicMessage);
        data.put("sourceHint", "请在 Nacos 中修改 Data ID = shop-service.yaml 里的 shop.dynamic-message");
        return Result.success(data);
    }
}
