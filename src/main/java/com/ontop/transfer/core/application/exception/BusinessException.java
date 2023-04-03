package com.ontop.transfer.core.application.exception;

import com.ontop.transfer.core.domain.model.TransferError;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final TransferError error;

    public BusinessException(TransferError error) {
        this.error = error;
    }
}
