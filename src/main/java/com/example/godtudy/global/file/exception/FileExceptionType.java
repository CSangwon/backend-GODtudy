package com.example.godtudy.global.file.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FileExceptionType{

    FILE_CAN_NOT_SAVE(10000, HttpStatus.BAD_REQUEST, "파일 저장에 실패했습니다."),
    FILE_CAN_NOT_DELETE(10001, HttpStatus.BAD_REQUEST, "파일 삭제에 실패했습니다.");

    private final int errorCode;
    private final HttpStatus httpStatus;
    private final String errorMessage;
}
