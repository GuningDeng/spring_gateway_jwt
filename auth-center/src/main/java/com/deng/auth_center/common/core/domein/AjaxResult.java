package com.deng.auth_center.common.core.domein;

import java.util.HashMap;

public class AjaxResult extends HashMap<String, Object> {
    public static final int SUCCESS = 200;
    public static final int ERROR = 500;

    public AjaxResult() {}

    public AjaxResult(int code, String msg) {
        this.put("code", code);
        this.put("msg", msg);
    }

    public AjaxResult(int code, String msg, Object data) {
        this.put("code", code);
        this.put("msg", msg);
        this.put("data", data);
    }

    public static AjaxResult success() {
        return new AjaxResult(SUCCESS, "Success");
    }

    public static AjaxResult success(Object data) {
        return new AjaxResult(SUCCESS, "Success", data);
    }
    
    public static AjaxResult success(String msg) {
        return new AjaxResult(SUCCESS, msg);
    }

    public static AjaxResult success(String msg, Object data) {
        return new AjaxResult(SUCCESS, msg, data);
    }

    public static AjaxResult error() {
        return new AjaxResult(ERROR, "Error");
    }

    public static AjaxResult error(String msg) {
        return new AjaxResult(ERROR, msg);
    }

    public static AjaxResult error(int code, String msg) {
        return new AjaxResult(code, msg);
    }
    
}
