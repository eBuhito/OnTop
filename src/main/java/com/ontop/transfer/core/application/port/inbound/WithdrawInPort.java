package com.ontop.transfer.core.application.port.inbound;

import com.ontop.transfer.adapter.inbound.rest.controller.dto.WithdrawRequestDto;
import com.ontop.transfer.core.domain.model.WithdrawRequest;
import com.ontop.transfer.core.domain.model.WithdrawResponse;

public interface WithdrawInPort {
    WithdrawResponse withdraw(WithdrawRequest withdrawRequest);
}
