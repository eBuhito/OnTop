package com.ontop.transfer.adapter.outbound.rest.wallet.balance;

import com.ontop.transfer.adapter.outbound.rest.wallet.balance.dto.BalanceResponseDto;
import com.ontop.transfer.core.application.port.outbound.WalletOutPort;
import com.ontop.transfer.core.domain.model.Balance;
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
public class WalletClient implements WalletOutPort {
    private static final Logger logger = LoggerFactory.getLogger(WalletClient.class);
    private final RestTemplate restTemplate;
    @Value(value = "${api.wallet.url}")
    private String walletApiUrl;
    @Value(value = "${api.wallet.balance.path}")
    private String balancePath;

    public WalletClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Balance getBalance(long userId) {
        String url = buildUrl(userId);
        HttpEntity<?> entity = buildEntity();
        logger.info("getBalance: Start exchanging GET {} with entity {}", url, entity);
        BalanceResponseDto response = null;
        try {
            response = restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            entity,
                            BalanceResponseDto.class)
                    .getBody();
        } catch (HttpStatusCodeException hsce) {
            logger.error("getBalance: Error exchanging GET {} - httpStatus: {} - response: {}", url, hsce.getStatusCode(), hsce.getResponseBodyAsString());
//            if (HttpStatus.INTERNAL_SERVER_ERROR.equals(hsce.getStatusCode())) {
//                throw new TaxesException(CodeErrors.TAXES_400);
//            }
//            throw new MerchantTaxesException(hsce);
        } catch (Exception e) {
            logger.error("getBalance: Error exchanging GET {} - message: {}", url, e.getMessage());
//            throw new TaxesException(CodeErrors.TAXES_300);
        }

        logger.info("getBalance: End exchanging GET {} ended successfully with response {}", url, response);
        return response.toDomain();
    }

    private String buildUrl(long userId) {
        return UriComponentsBuilder.fromHttpUrl(walletApiUrl + balancePath)
                .buildAndExpand(userId)
                .toUriString();
    }

    private HttpEntity buildEntity() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(headers);
    }

}
