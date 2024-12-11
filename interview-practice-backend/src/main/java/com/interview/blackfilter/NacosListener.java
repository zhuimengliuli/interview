package com.interview.blackfilter;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

/**
 * @author hjc
 * @version 1.0
 */
@Slf4j
@Component
public class NacosListener implements InitializingBean {
    @NacosInjected
    private ConfigService configService;

    @Value("${nacos.config.data-id}")
    private String dataId;

    @Value("${nacos.config.group}")
    private String group;
    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Nacos 监听器启动");
        String config = configService.getConfigAndSignListener(dataId, group, 5000, new Listener() {
            @Override
            public void receiveConfigInfo(String configInfo) {
                log.info("Nacos 监听器接收到配置信息:{}", configInfo);
                BlackIpUtils.rebuildBlackIp(configInfo);
            }

            @Override
            public Executor getExecutor() {
                return null;
            }

        });
        BlackIpUtils.rebuildBlackIp(config);
    }
}
