package com.pro.jgsu.common;

public class CustomException extends RuntimeException{

    /**
     * 自定义业务异常类
     * @param message
     */
    public CustomException(String message){
        super(message);
    }
}
