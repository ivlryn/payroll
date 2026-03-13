package org.aub.payzenapi.base;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BaseController {

//    this is for no payload
    protected ResponseEntity<ApiResponse<Object>> response(String message) {
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message(message)
                .build());
    }

//    this is for http ok mostly just use for get
    protected <T> ResponseEntity<ApiResponse<T>> response(String message, T payload) {
        ApiResponse<T> apiResponse = ApiResponse.<T>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message(message)
                .payload(payload)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

//    this is for create
    protected <T> ResponseEntity<ApiResponse<T>> response(String message, HttpStatus httpStatus, T payload) {
        ApiResponse<T> apiResponse = ApiResponse.<T>builder()
                .success(true)
                .status(httpStatus.value())
                .message(message)
                .payload(payload)
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    protected <T> ResponseEntity<ApiResponse<T>> response(String message, T payload, Integer page, Integer size, Integer totalCount) {
        ApiResponse<T> apiResponse = ApiResponse.<T>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message(message)
                .payload(payload)
                .paginationResponse(new PaginationResponse(page, size, totalCount))
                .build();
        return ResponseEntity.ok(apiResponse);
    }

}
