package com.example.godtudy.global.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class ResultResponse<T> {

    private T response;

    private HttpStatus status;

}
