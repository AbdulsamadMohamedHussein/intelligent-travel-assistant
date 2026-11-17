package com.example.travelbackend.vo;

import lombok.Data;

/** Unified response wrapper returned by every REST endpoint. */
@Data
public class Result<T> {
    private Boolean success;
    private Integer code;
    private String message;
    private String error;
    private String errorResponse;
    private T data;

    public static <T> Result<T> ok() {
        Result<T> result = new Result<>();
        result.setSuccess(true);
        result.setCode(200);
        result.setMessage("success");
        return result;
    }

    public static <T> Result<T> ok(T data) {
        Result<T> result = Result.ok();
        result.setData(data);
        return result;
    }

    public static <T> Result<T> fail() {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setCode(500);
        return result;
    }

    public static <T> Result<T> fail(String message) {
        Result<T> result = Result.fail();
        result.setMessage(message);
        return result;
    }

    /** Failure with a specific code, e.g. 400 for invalid input or 404 for not found. */
    public static <T> Result<T> fail(Integer code, String message) {
        Result<T> result = Result.fail(message);
        result.setCode(code);
        return result;
    }

    public static <T> Result<T> error(String errorResponse, String error) {
        Result<T> result = new Result<>();
        result.setSuccess(false);
        result.setError(error);
        result.setErrorResponse(errorResponse);
        return result;
    }
}
