package com.catalogue.dto;

import java.time.LocalDateTime;

/**
 * Generic API response wrapper for standardizing responses.
 *
 * @param <T> The type of data contained in the response
 */
public class ApiResponse<T> {

    private LocalDateTime timestamp;
    private int status;
    private String message;
    private T data;

    /**
     * Default constructor.
     */
    public ApiResponse() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructor with status, message, and data.
     *
     * @param status HTTP status code
     * @param message Response message
     * @param data Response data
     */
    public ApiResponse(int status, String message, T data) {
        this();
        this.status = status;
        this.message = message;
        this.data = data;
    }

    /**
     * Success response factory method.
     *
     * @param <T> Type of the data
     * @param message Success message
     * @param data Response data
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    /**
     * Error response factory method.
     *
     * @param <T> Type of the data
     * @param status HTTP status code
     * @param message Error message
     * @return ApiResponse instance
     */
    public static <T> ApiResponse<T> error(int status, String message) {
        return new ApiResponse<>(status, message, null);
    }

    // Getters and setters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
