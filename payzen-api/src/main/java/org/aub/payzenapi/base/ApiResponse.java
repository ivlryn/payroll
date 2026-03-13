package org.aub.payzenapi.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private Boolean success;
    private int status;
    private String message;
    private T payload;
    private PaginationResponse paginationResponse;
    private final LocalDateTime timestamp = LocalDateTime.now();
}