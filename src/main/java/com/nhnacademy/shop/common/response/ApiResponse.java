package com.nhnacademy.shop.common.response;

import jakarta.annotation.Nullable;
import lombok.Getter;

@Getter
public class ApiResponse<T> {
    private final Boolean success;
    private final T data;
    private final ApiResponseMetaData meta;

    private ApiResponse(Boolean success, T data, ApiResponseMetaData meta) {
        this.success = success;
        this.data = data;
        this.meta = meta;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, ApiResponseMetaData.success());
    }

    public static <T> ApiResponse<T> fail(T data, String errorCode, String errorMessage) {
        return new ApiResponse<>(false, data, ApiResponseMetaData.fail(errorCode, errorMessage));
    }

}

@Getter
class ApiResponseMetaData {
    private final @Nullable String errorCode;
    private final String message;


    private ApiResponseMetaData(@Nullable String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public static ApiResponseMetaData success() {
        return new ApiResponseMetaData(null, "성공");
    }

    public static ApiResponseMetaData fail(String errorCode, String message) {
        return new ApiResponseMetaData(errorCode, message);
    }
}