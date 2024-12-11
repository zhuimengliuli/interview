package com.interview.blackfilter;

import cn.hutool.bloomfilter.BitMapBloomFilter;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;

/**
 * @author hjc
 * @version 1.0
 */
@Slf4j
public class BlackIpUtils {
    public static BitMapBloomFilter bloomFilter;

    public static boolean isBlackIp(String ip) {
        return bloomFilter.contains(ip);
    }

    /**
     * 重建IP黑名单
     * @param config
     */
    public static void rebuildBlackIp(String config) {
        if (StringUtils.isBlank(config)) {
            config = "{}";
        }
        Yaml yaml = new Yaml();
        Map map = yaml.loadAs(config, Map.class);
        List<String> blackIpList = (List<String>) map.get("blackIpList");
        synchronized (BlackIpUtils.class) {
            if (CollectionUtils.isNotEmpty(blackIpList)) {
                bloomFilter = new BitMapBloomFilter(958506);
                for (String blackIp : blackIpList) {
                    bloomFilter.add(blackIp);
                }
            } else {
                bloomFilter = new BitMapBloomFilter(100);
            }
        }

    }
}
