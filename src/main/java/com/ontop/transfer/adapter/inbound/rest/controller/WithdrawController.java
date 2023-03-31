package com.ontop.transfer.adapter.inbound.rest.controller;

import com.ontop.transfer.adapter.inbound.rest.controller.dto.WithdrawRequestDto;
import com.ontop.transfer.adapter.inbound.rest.controller.dto.WithdrawResponseDto;
import com.ontop.transfer.core.application.port.inbound.WithdrawInPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@Validated
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
                WithdrawResponseDto.from(
                        withdrawInPort.withdraw(withdrawRequestDto.toDomain())
                )
        );
    }

}