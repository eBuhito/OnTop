package com.ontop.transfer.adapter.inbound.rest.controller.advice;

import com.ontop.transfer.adapter.inbound.rest.controller.dto.ErrorResponse;
import com.ontop.transfer.adapter.inbound.rest.controller.handler.ErrorHandler;
import com.ontop.transfer.core.application.exception.BusinessException;
import com.ontop.transfer.core.domain.model.TransferError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice(annotations = ErrorHandler.class)
public class CustomControllerAdvice {
    private static final Logger logger = LoggerFactory.getLogger(CustomControllerAdvice.class);

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public ErrorResponse handleBusinessException(BusinessException ex) {
        logger.error("BusinessException: ", ex);
        TransferError error = ex.getError();
        return ErrorResponse.builder()
                .status(error.getCode().value())
                .error(error.getError())
                .build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public List<ErrorResponse> handleConstraintViolationException(ConstraintViolationException cve) {
        logger.error("ConstraintViolationException encountered: ", cve);
        return cve.getConstraintViolations().stream()
                .map(violation -> ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(violation.getMessage())
                        .build())
                .collect(Collectors.toList());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException matme) {
        logger.error("MethodArgumentTypeMismatchException encountered: ", matme);
        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(matme.getMessage())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        logger.error("MethodArgumentNotValidException: ", ex);
        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(ex.getFieldError().getDefaultMessage())
                .build();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleException(Exception e) {
        logger.error("Exception encountered: ", e);
        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(e.getMessage())
                .build();
    }
}
