package com.example.BackendServer.common.exception;

import com.example.BackendServer.common.response.BaseResponse;
import com.example.BackendServer.common.response.BaseResponseStatus;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.example.BackendServer.common.response.BaseResponseStatus.*;
import static com.example.BackendServer.common.response.BaseResponseStatus.EMPTY_TOKEN_CLAIM;

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

   /* catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
        log.info("Invalid JWT Token", e);
        setErrorResponse(response, INVALID_TOKEN);
    } catch (
    ExpiredJwtException e) {
        log.info("Expired JWT Token", e);
        setErrorResponse(response, EXPRIED_TOKEN);
    } catch (
    UnsupportedJwtException e) {
        log.info("Unsupported JWT Token", e);
        setErrorResponse(response, UNSURPPORTED_TOKEN);
    } catch (IllegalArgumentException e) {
        log.info("JWT claims string is empty.", e);
        setErrorResponse(response, EMPTY_TOKEN_CLAIM);
    }*/
    // jwt exception
   @ExceptionHandler(value = {io.jsonwebtoken.security.SecurityException.class, MalformedJwtException.class})
   public BaseResponse<BaseResponseStatus> handleJwtException1(){

       return new BaseResponse<>(INVALID_TOKEN);
   }
   @ExceptionHandler(value = ExpiredJwtException.class)
   public BaseResponse<BaseResponseStatus> handleJwtException2(){

       return new BaseResponse<>(EXPRIED_TOKEN);
   }
    @ExceptionHandler(value = UnsupportedJwtException.class)
    public BaseResponse<BaseResponseStatus> handleJwtException3(){

        return new BaseResponse<>(UNSURPPORTED_TOKEN);
    }
    @ExceptionHandler(value = IllegalArgumentException.class)
    public BaseResponse<BaseResponseStatus> handleJwtException4(){

        return new BaseResponse<>(EMPTY_TOKEN_CLAIM);
    }

}
