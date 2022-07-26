package com.example.godtudy.global.file.exception;

public class FileException extends RuntimeException{

    public FileException(){}

    public FileException(FileExceptionType fileExceptionType) {
        super(fileExceptionType.getErrorMessage() + " " + fileExceptionType.getErrorCode());
    }
}
