package com.interview.satoken;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.interview.common.ErrorCode;
import com.interview.exception.ThrowUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 设备工具类
 * @author hjc
 * @version 1.0
 */
public class DeviceUtils {
    /**
     * 根据请求获取设备
     * @param request
     * @return
     */
    public static String getRequestDevice(HttpServletRequest request) {
        String userAgentStr = request.getHeader(Header.USER_AGENT.toString());
        UserAgent userAgent = UserAgentUtil.parse(userAgentStr);
        ThrowUtils.throwIf(userAgent == null, ErrorCode.OPERATION_ERROR, "获取设备信息失败");
        // 默认为PC
        String device = "PC";
        if (isMiniProgram(userAgentStr)) {
            // 判断是否为小程序
            device = "miniProgram";
        } else if (isPad(userAgentStr)) {
            // 判断是否为Pad
            device = "pad";
        } else if (userAgent.isMobile()) {
            // 判断是否为手机
            device = "mobile";
        }
        return device;
    }

    /**
     * 判断是否为小程序
     * @param userAgentStr
     * @return
     */
    public static boolean isMiniProgram(String userAgentStr) {
        return StrUtil.containsIgnoreCase(userAgentStr, "miniProgram")
                && StrUtil.containsIgnoreCase(userAgentStr, "MicroMessenger");
    }

    /**
     * 判断是否为平板
     * @param userAgentStr
     * @return
     */
    public static boolean isPad(String userAgentStr) {
        // 判断是否为Pad
        boolean isPad = StrUtil.containsIgnoreCase(userAgentStr, "iPad");
        // 判断是否为Android平板
        boolean isAndroidTablet = StrUtil.containsIgnoreCase(userAgentStr, "Android")
                && !StrUtil.containsIgnoreCase(userAgentStr, "Mobile");
        return isPad || isAndroidTablet;
    }
}
