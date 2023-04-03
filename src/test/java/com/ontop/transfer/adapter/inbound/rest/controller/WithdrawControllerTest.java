package com.ontop.transfer.adapter.inbound.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ontop.transfer.adapter.inbound.rest.controller.advice.CustomControllerAdvice;
import com.ontop.transfer.adapter.inbound.rest.controller.dto.WithdrawRequestDto;
import com.ontop.transfer.core.application.exception.BusinessException;
import com.ontop.transfer.core.application.port.inbound.WithdrawInPort;
import com.ontop.transfer.core.domain.model.TransferError;
import com.ontop.transfer.core.domain.model.WithdrawResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class WithdrawControllerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private WithdrawInPort withdrawInPort;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        WithdrawController withdrawController = new WithdrawController(withdrawInPort);
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(withdrawController)
                .setControllerAdvice(new CustomControllerAdvice())
                .build();
    }

    @Test
    void successWithdraw() throws Exception {
        WithdrawRequestDto withdrawRequestDto = new WithdrawRequestDto("22", 333L, 123.45);
        WithdrawResponse withdrawResponse = new WithdrawResponse(
                "22", 333L, 123.45, 1.23
        );
        when(withdrawInPort.withdraw(withdrawRequestDto.toWithdraw())).thenReturn(withdrawResponse);

        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/transfers/api/v1/withdraws")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildRequest(withdrawRequestDto))
        );
        result
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.destination_bank_account_id").value("22"))
                .andExpect(jsonPath("$.user_id").value("333"))
                .andExpect(jsonPath("$.amount").value(123.45))
                .andExpect(jsonPath("$.fee_amount").value(1.23));
    }

    @Test
    void failWithdraw() throws Exception {
        String requestJson = objectMapper.writeValueAsString(null);

        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/transfers/api/v1/withdraws")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
        );
        result
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Required request body is missing: public org.springframework.http.ResponseEntity<com.ontop.transfer.adapter.inbound.rest.controller.dto.WithdrawResponseDto> com.ontop.transfer.adapter.inbound.rest.controller.WithdrawController.withdraw(com.ontop.transfer.adapter.inbound.rest.controller.dto.WithdrawRequestDto)"));
    }

    @Test
    void failWithdrawWithBusinessException() throws Exception {
        WithdrawRequestDto withdrawRequestDto = new WithdrawRequestDto("22", 333L, 123.45);
        TransferError transferError = new TransferError(HttpStatus.UNPROCESSABLE_ENTITY, "algun error");
        when(withdrawInPort.withdraw(withdrawRequestDto.toWithdraw()))
                .thenThrow(new BusinessException(transferError));

        ResultActions result = mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/transfers/api/v1/withdraws")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(buildRequest(withdrawRequestDto))
        );
        result
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("algun error"));
    }

    private String buildRequest(WithdrawRequestDto withdrawRequestDto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(withdrawRequestDto);
    }
}