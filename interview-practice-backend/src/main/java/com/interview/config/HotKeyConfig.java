package com.interview.config;

import com.jd.platform.hotkey.client.ClientStarter;
import lombok.Data;
import org.elasticsearch.http.HttpStats;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * HotKey配置
 *
 *
 */
@Configuration
@ConfigurationProperties(prefix = "hotkey")
@Data
public class HotKeyConfig{
    /**
     * etcd服务完整地址
     */
    private String etcdServer = "http://127.0.0.1:2379";

    /**
     * 应用名称
     */
    private String appName = "interview";

    /**
     * 本地缓存最大数量
     */
    private int caffeineSize = 10000;

    /**
     * 批量推送 key 的间隔时间
     */
    private Long pushPeriod = 1000L;

    @Bean
    public void initHotKey() {
        ClientStarter.Builder builder = new ClientStarter.Builder();
        ClientStarter clientStarter = builder.setAppName(appName)
                .setCaffeineSize(caffeineSize)
                .setEtcdServer(etcdServer)
                .setPushPeriod(pushPeriod).build();
        clientStarter.startPipeline();
    }
}
