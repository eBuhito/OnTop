package com.ontop.transfer.core.application.exception;

import com.ontop.transfer.core.domain.model.TransferError;
import org.springframework.http.HttpStatus;

public class BankAccountNotFoundException extends BusinessException {
    public BankAccountNotFoundException() {
        super(new TransferError(
                HttpStatus.BAD_REQUEST,
                "Bank account not found"));
    }
}
