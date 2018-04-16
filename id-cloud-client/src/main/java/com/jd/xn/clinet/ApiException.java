package com.jd.xn.clinet;

/**
 * 定义的Api异常
 *
 * @author lijizhen1@jd.com
 * @date 2018/4/16 14:06
 */
public class ApiException extends Exception {
    private String errCode;
    private String errMsg;
    private String subErrCode;
    private String subErrMsg;

    public ApiException() {
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApiException(String message) {
        super(message);
    }

    public ApiException(Throwable cause) {
        super(cause);
    }

    public ApiException(String errCode, String errMsg) {
        super(errCode + ":" + errMsg);
        this.errCode = errCode;
        this.errMsg = errMsg;
    }

    public ApiException(String errCode, String errMsg,
                        String subErrCode, String subErrMsg) {
        super(errCode + ":" + errMsg + ":" + subErrCode + ":" + subErrMsg);
        this.errCode = errCode;
        this.errMsg = errMsg;
        this.subErrCode = subErrCode;
        this.subErrMsg = subErrMsg;
    }

    public String getErrCode() {
        return errCode;
    }


    public String getErrMsg() {
        return errMsg;
    }

    public String getSubErrCode() {
        return subErrCode;
    }

    public String getSubErrMsg() {
        return subErrMsg;
    }
}
