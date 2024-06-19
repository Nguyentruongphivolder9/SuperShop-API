package com.project.supershop.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResultResponse<T> {
    protected T data;
    protected String timeStamp;
    protected String message;
    protected Integer statusCode;
    protected HttpStatus status;
}
