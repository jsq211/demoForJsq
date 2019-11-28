package com.jsq.demo.pojo.dto;

/**
 * 调用方法DTO
 * @author jsq
 */
public class TraceDTO {
    /**
     * 当前追踪方法
     */
    private String methodName;
    /**
     * 父级调用方法
     */
    private String parentMethodName;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getParentMethodName() {
        return parentMethodName;
    }

    public void setParentMethodName(String parentMethodName) {
        this.parentMethodName = parentMethodName;
    }
}
