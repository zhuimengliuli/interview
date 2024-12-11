package com.interview.sentinel;

import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.circuitbreaker.CircuitBreakerStrategy;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowItem;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author hjc
 * @version 1.0
 */

@Component
public class SentinelRulesManager {
    @PostConstruct
    public void initRules() {
        initFlowRules();
        initDegradeRules();
    }

    // 热点限流规则
    public void initFlowRules() {
        ParamFlowRule rule = new ParamFlowRule("listQuestionVOByPage")
                .setParamIdx(0)
                .setCount(20)
                .setDurationInSec(10);
        ParamFlowRuleManager.loadRules(Collections.singletonList(rule));
    }
    // 降级规则
    public void initDegradeRules() {
        DegradeRule slowCallRule = new DegradeRule("listQuestionVOByPage")
        .setGrade(CircuitBreakerStrategy.SLOW_REQUEST_RATIO.getType())
        .setCount(0.1)
        .setMinRequestAmount(10)
                .setStatIntervalMs(30 * 1000)
                .setTimeWindow(10);
        DegradeRule errorCallRule = new DegradeRule("listQuestionVOByPage")
                .setGrade(CircuitBreakerStrategy.ERROR_RATIO.getType())
                .setCount(0.1)
                .setMinRequestAmount(10)
                .setStatIntervalMs(30 * 1000)
                .setTimeWindow(10);
        DegradeRuleManager.loadRules(Arrays.asList(slowCallRule, errorCallRule));
    }
}
