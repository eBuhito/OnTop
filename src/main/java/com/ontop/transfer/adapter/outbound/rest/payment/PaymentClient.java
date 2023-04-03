package com.ontop.transfer.adapter.outbound.rest.payment;

import com.ontop.transfer.adapter.outbound.rest.config.RestConfig;
import com.ontop.transfer.adapter.outbound.rest.payment.dto.PaymentRequestDto;
import com.ontop.transfer.adapter.outbound.rest.payment.dto.PaymentResponseDto;
import com.ontop.transfer.core.application.port.outbound.PaymentOutPort;
import com.ontop.transfer.core.domain.model.BankAccount;
import com.ontop.transfer.core.domain.model.PaymentInfo;
import com.ontop.transfer.core.domain.model.TransferError;
import io.vavr.control.Either;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class PaymentClient implements PaymentOutPort {
    private static final Logger logger = LoggerFactory.getLogger(PaymentClient.class);
    private final RestTemplate restTemplate;
    private final RestConfig config;

    public PaymentClient(RestTemplate restTemplate, RestConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    @Override
    public Either<TransferError, PaymentInfo> executePayment(BankAccount source, BankAccount destination, double netAmount) {
        String url = buildUrl();
        HttpEntity<?> entity = buildEntity(PaymentRequestDto.from(source, destination, netAmount));
        logger.info("executePayment: Start exchanging POST {} with entity {}", url, entity);
        PaymentResponseDto response;
        try {
            response = restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            entity,
                            PaymentResponseDto.class)
                    .getBody();
        } catch (HttpStatusCodeException hsce) {
            logger.error("executePayment: Error exchanging POST {} - httpStatus: {} - response: {}", url, hsce.getStatusCode(), hsce.getResponseBodyAsString());
            return Either.left(new TransferError(hsce.getStatusCode(), hsce.getResponseBodyAsString()));
        } catch (Exception e) {
            logger.error("executePayment: Error exchanging POST {} - message: {}", url, e.getMessage());
            return Either.left(new TransferError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
        }

        logger.info("executePayment: End exchanging POST {} ended successfully with response {}", url, response);
        assert response != null;
        return Either.right(response.toPaymentInfo());
    }

    private String buildUrl() {
        return UriComponentsBuilder.fromHttpUrl(config.getPaymentApiUrl() + config.getPaymentPath())
                .toUriString();
    }

    private HttpEntity<?> buildEntity(PaymentRequestDto paymentRequestDto) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(paymentRequestDto, headers);
    }
}
