package com.example.BackendServer.common.exception;

import com.example.BackendServer.common.response.BaseResponse;
import com.example.BackendServer.common.response.BaseResponseStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(value = BaseException.class)
    public BaseResponse<BaseResponseStatus> handleBaseException(BaseException ex){

        return new BaseResponse<>(ex.getStatus());
    }
    @ExceptionHandler(value = MethodArgumentNotValidException.class) // @Valid 관련 Exception 처리
    public BaseResponse<List> handleBaseException(MethodArgumentNotValidException ex){
        BaseResponse<List> response = new BaseResponse<>(BaseResponseStatus.WRONG_REQUEST_BODY);
        List<String> messages = ex.getBindingResult().getFieldErrors().stream().map(error -> error.getDefaultMessage()).collect(Collectors.toList());

        response.notValid(messages);

        return response;
    }

}
