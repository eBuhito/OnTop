package com.ontop.transfer.adapter.inbound.rest.controller;

import com.ontop.transfer.adapter.inbound.rest.controller.dto.TransferResultDto;
import com.ontop.transfer.adapter.inbound.rest.controller.dto.WithdrawRequestDto;
import com.ontop.transfer.adapter.inbound.rest.controller.dto.WithdrawRequestedDto;
import com.ontop.transfer.adapter.inbound.rest.controller.dto.WithdrawResponseDto;
import com.ontop.transfer.adapter.inbound.rest.controller.handler.ErrorHandler;
import com.ontop.transfer.core.application.port.inbound.WithdrawInPort;
import com.ontop.transfer.core.domain.model.WithdrawResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@ErrorHandler
@RequestMapping(value = "/transfers/api")
public class WithdrawController {

    private static final Logger logger = LoggerFactory.getLogger(WithdrawController.class);
    private final WithdrawInPort withdrawInPort;

    public WithdrawController(WithdrawInPort withdrawInPort) {
        this.withdrawInPort = withdrawInPort;
    }

    @PostMapping(
            value = "/v1/withdraws",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<WithdrawResponseDto> withdraw(
            @Valid @NotNull(message = "withdraw request is required.")
            @RequestBody WithdrawRequestDto withdrawRequestDto
    ) {
        logger.info("Start execution of POST /transfers/api/v1/withdraws with payload: {}", withdrawRequestDto);
        return ResponseEntity.ok(
                buildResponseDto(
                        withdrawInPort.withdraw(withdrawRequestDto.toWithdraw())
                )
        );
    }

    private WithdrawResponseDto buildResponseDto(WithdrawResponse withdraw) {
        return WithdrawResponseDto.builder()
                .request(WithdrawRequestedDto.builder()
                        .userId(withdraw.getRequest().getUserId())
                        .destinationBankAccountId(withdraw.getRequest().getDestinationBankAccountId())
                        .amount(withdraw.getRequest().getAmount())
                        .build())
                .result(TransferResultDto.builder()
                        .transferProviderId(withdraw.getResult().getTransferProviderId())
                        .amountTransferred(withdraw.getResult().getAmountTransferred())
                        .amountFee(withdraw.getResult().getAmountFee())
                        .status(withdraw.getResult().getStatus().name())
                        .build())
                .build();
    }

}
