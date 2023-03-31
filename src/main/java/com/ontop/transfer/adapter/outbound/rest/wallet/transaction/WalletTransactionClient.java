package com.ontop.transfer.adapter.outbound.rest.wallet.transaction;

import com.ontop.transfer.adapter.outbound.rest.wallet.balance.WalletClient;
import com.ontop.transfer.adapter.outbound.rest.wallet.transaction.dto.DiscountRequestDto;
import com.ontop.transfer.adapter.outbound.rest.wallet.transaction.dto.WalletTransactionResponseDto;
import com.ontop.transfer.core.application.port.outbound.WalletTransactionOutPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class WalletTransactionClient implements WalletTransactionOutPort {
    private static final Logger logger = LoggerFactory.getLogger(WalletClient.class);
    private final RestTemplate restTemplate;
    @Value(value = "${api.wallet.url}")
    private String walletApiUrl;
    @Value(value = "${api.wallet.transaction.path}")
    private String transactionPath;

    public WalletTransactionClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public long discountFunds(long userId, double amount) {
        String url = buildUrl();
        HttpEntity<?> entity = buildEntity(DiscountRequestDto.from(userId, amount));
        logger.info("discountFunds: Start exchanging POST {} with entity {}", url, entity);
        WalletTransactionResponseDto response = null;
        try {
            response = restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            entity,
                            WalletTransactionResponseDto.class)
                    .getBody();
        } catch (HttpStatusCodeException hsce) {
            logger.error("discountFunds: Error exchanging POST {} - httpStatus: {} - response: {}", url, hsce.getStatusCode(), hsce.getResponseBodyAsString());
//            if (HttpStatus.INTERNAL_SERVER_ERROR.equals(hsce.getStatusCode())) {
//                throw new TaxesException(CodeErrors.TAXES_400);
//            }
//            throw new MerchantTaxesException(hsce);
        } catch (Exception e) {
            logger.error("discountFunds: Error exchanging POST {} - message: {}", url, e.getMessage());
//            throw new TaxesException(CodeErrors.TAXES_300);
        }

        logger.info("discountFunds: End exchanging POST {} ended successfully with response {}", url, response);
        return response.getWalletTransactionId();
    }

    private String buildUrl() {
        return UriComponentsBuilder.fromHttpUrl(walletApiUrl + transactionPath)
                .toUriString();
    }

    private HttpEntity<?> buildEntity(DiscountRequestDto discountRequestDto) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(discountRequestDto, headers);
    }
}
