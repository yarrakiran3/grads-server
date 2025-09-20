package com.grads.dto;

import lombok.Data;

// API Response DTOs
@Data
public class ApiResponseDTO<T> {
    private boolean success;
    private String message;
    private T data;
    private String error;

    public static <T> ApiResponseDTO<T> success(T data) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.setSuccess(true);
        response.setData(data);
        return response;
    }

    public static <T> ApiResponseDTO<T> success(T data, String message) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> ApiResponseDTO<T> error(String error) {
        ApiResponseDTO<T> response = new ApiResponseDTO<>();
        response.setSuccess(false);
        response.setError(error);
        return response;
    }
}
