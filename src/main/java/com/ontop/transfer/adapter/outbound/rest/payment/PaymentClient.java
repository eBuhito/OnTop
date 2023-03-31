package com.ontop.transfer.adapter.outbound.rest.payment;

import com.ontop.transfer.adapter.outbound.rest.payment.dto.PaymentRequestDto;
import com.ontop.transfer.adapter.outbound.rest.payment.dto.PaymentResponseDto;
import com.ontop.transfer.adapter.outbound.rest.wallet.transaction.dto.DiscountRequestDto;
import com.ontop.transfer.adapter.outbound.rest.wallet.transaction.dto.WalletTransactionResponseDto;
import com.ontop.transfer.core.application.port.outbound.PaymentOutPort;
import com.ontop.transfer.core.domain.model.BankAccount;
import com.ontop.transfer.core.domain.model.PaymentInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class PaymentClient implements PaymentOutPort {
    private static final Logger logger = LoggerFactory.getLogger(PaymentClient.class);
    private final RestTemplate restTemplate;
    @Value(value = "${api.payment.url}")
    private String paymentApiUrl;
    @Value(value = "${api.payment.path}")
    private String paymentPath;

    public PaymentClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public PaymentInfo executePayment(BankAccount source, BankAccount destination, double netAmount) {
        String url = buildUrl();
        HttpEntity<?> entity = buildEntity(PaymentRequestDto.from(source, destination, netAmount));
        logger.info("executePayment: Start exchanging POST {} with entity {}", url, entity);
        PaymentResponseDto response = null;
        try {
            response = restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            entity,
                            PaymentResponseDto.class)
                    .getBody();
        } catch (HttpStatusCodeException hsce) {
            logger.error("executePayment: Error exchanging POST {} - httpStatus: {} - response: {}", url, hsce.getStatusCode(), hsce.getResponseBodyAsString());
//            if (HttpStatus.INTERNAL_SERVER_ERROR.equals(hsce.getStatusCode())) {
//                throw new TaxesException(CodeErrors.TAXES_400);
//            }
//            throw new MerchantTaxesException(hsce);
        } catch (Exception e) {
            logger.error("executePayment: Error exchanging POST {} - message: {}", url, e.getMessage());
//            throw new TaxesException(CodeErrors.TAXES_300);
        }

        logger.info("executePayment: End exchanging POST {} ended successfully with response {}", url, response);
        return response.toPaymentInfo();
    }

    private String buildUrl() {
        return UriComponentsBuilder.fromHttpUrl(paymentApiUrl + paymentPath)
                .toUriString();
    }

    private HttpEntity<?> buildEntity(PaymentRequestDto paymentRequestDto) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(paymentRequestDto, headers);
    }
}
