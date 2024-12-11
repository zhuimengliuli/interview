package com.interview.aop;

import com.interview.annotation.HotKeyCache;
import com.interview.common.ErrorCode;
import com.interview.common.HotKeyEnum;
import com.interview.common.ResultUtils;
import com.interview.constant.HotKeyConstant;
import com.interview.exception.ThrowUtils;
import com.interview.model.vo.BankVO;
import com.jd.platform.hotkey.client.callback.JdHotKeyStore;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import javax.crypto.KeyGenerator;
import java.lang.reflect.Method;

/**
 * @author hjc
 * @version 1.0
 */
@Aspect
@Component
@Slf4j
public class HotKeyInterceptor {
    private final ExpressionParser parser = new SpelExpressionParser();

    /**
     * 执行拦截
     * @param joinPoint
     * @param hotKeyCache
     * @return
     */
    @Around("@annotation(hotKeyCache)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, HotKeyCache hotKeyCache) throws Throwable {
        String keyExpression = hotKeyCache.key();
        HotKeyEnum type = hotKeyCache.type();
        // 根据 SpEL 表达式生成key
        String key = generateKey(joinPoint, keyExpression);
        ThrowUtils.throwIf(key == null, ErrorCode.PARAMS_ERROR,"key不能为空");
        if (type == HotKeyEnum.QUESTION){
            key = HotKeyConstant.QUESTION_HOT_KEY_PREFIX + key;
        } else {
            key = HotKeyConstant.BANK_HOT_KEY_PREFIX + key;
        }
        if (JdHotKeyStore.isHotKey(key)){
            Object cacheVO = JdHotKeyStore.get(key);
            if (cacheVO != null){
                log.info("缓存命中，key:{}， value:{}", key, cacheVO);
                JdHotKeyStore.smartSet(key, cacheVO);
                return ResultUtils.success(cacheVO);
            }
        }
        return joinPoint.proceed();
    }

    public String generateKey(ProceedingJoinPoint joinPoint, String keyExpression) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        EvaluationContext context = new StandardEvaluationContext();
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = signature.getParameterNames();
        for (int i = 0; i < args.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }
        return parser.parseExpression(keyExpression).getValue(context, String.class);
    }
}
