package com.project.supershop.common;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.sql.Timestamp;
import java.util.Map;

@SuperBuilder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResultResponse{
    protected Map<?, ?> data;
    protected String timeStamp;
    protected String message;
    protected Integer statusCode;
    protected HttpStatus status;
    protected String developerMessage;
    protected String path;
    protected String requestMethod;

    
}