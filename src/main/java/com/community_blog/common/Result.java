package com.community_blog.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;

    private T data;

    private String message;

    public static <T> Result<T> success(T object) {
        Result<T> r = new Result<T>();
        r.data = object;
        r.code = 0;
        return r;
    }

    public static <T> Result<T> error(String message) {
        Result r = new Result();
        r.message = message;
        r.code = 1;
        return r;
    }
}
