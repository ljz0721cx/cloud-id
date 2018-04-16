package com.jd.xn.clinet;

/**
 * api规则校验
 *
 * @author lijizhen1@jd.com
 * @date 2018/4/16 16:53
 */
public class ApiRuleException extends ApiException {
    private static final long serialVersionUID = -7787145910600194272L;

    public ApiRuleException(String errCode, String errMsg) {
        super(errCode, errMsg);
    }
}