package com.jisungin.api;

import com.jisungin.exception.BusinessException;
import com.jisungin.exception.ErrorCode;
import com.jisungin.exception.ErrorResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String firstErrorMessage = bindingResult.getFieldErrors().get(0).getDefaultMessage();

        List<String> errors = bindingResult.getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toList();

        log.warn("MethodArgumentNotValidException: {}", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), firstErrorMessage));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissionServletRequestParamException(
            MissingServletRequestParameterException e
    ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(ErrorCode.INVALID_PARAMS_VALUE.getCode(),
                        ErrorCode.INVALID_PARAMS_VALUE.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.info("BusinessException: {}", e.getMessage());
        return ResponseEntity.status(e.getCode()).body(ErrorResponse.of(e.getCode(), e.getMessage()));
    }

}
