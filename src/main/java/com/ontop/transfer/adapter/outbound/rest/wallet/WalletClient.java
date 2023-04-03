package com.ontop.transfer.adapter.outbound.rest.wallet;

import com.ontop.transfer.adapter.outbound.rest.config.RestConfig;
import com.ontop.transfer.adapter.outbound.rest.wallet.dto.balance.BalanceResponseDto;
import com.ontop.transfer.adapter.outbound.rest.wallet.dto.transaction.FundsRequestDto;
import com.ontop.transfer.adapter.outbound.rest.wallet.dto.transaction.WalletTransactionResponseDto;
import com.ontop.transfer.core.application.port.outbound.WalletOutPort;
import com.ontop.transfer.core.domain.model.Balance;
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
public class WalletClient implements WalletOutPort {
    private static final Logger logger = LoggerFactory.getLogger(WalletClient.class);
    private final RestTemplate restTemplate;
    private final RestConfig config;

    public WalletClient(RestTemplate restTemplate, RestConfig restConfig) {
        this.restTemplate = restTemplate;
        this.config = restConfig;
    }

    @Override
    public Either<TransferError, Balance> getBalance(long userId) {
        String url = buildBalanceUrl(userId);
        HttpEntity<?> entity = buildBalanceEntity();
        logger.info("getBalance: Start exchanging GET {} with entity {}", url, entity);
        BalanceResponseDto response;
        try {
            response = restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            entity,
                            BalanceResponseDto.class)
                    .getBody();
            logger.info("getBalance: End exchanging GET {} ended successfully with response {}", url, response);
            assert response != null;
            return Either.right(response.toDomain());
        } catch (HttpStatusCodeException hsce) {
            logger.error("getBalance: Error exchanging GET {} - httpStatus: {} - response: {}", url, hsce.getStatusCode(), hsce.getResponseBodyAsString());
            return Either.left(new TransferError(hsce.getStatusCode(), hsce.getResponseBodyAsString()));
        } catch (Exception e) {
            logger.error("getBalance: Error exchanging GET {} - message: {}", url, e.getMessage());
            return Either.left(new TransferError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
        }
    }

    @Override
    public Either<TransferError, Long> moveFunds(long userId, double amount) {
        String url = buildTransactionUrl();
        HttpEntity<?> entity = buildTransactionEntity(FundsRequestDto.from(userId, amount));
        logger.info("moveFunds: Start exchanging POST {} with entity {}", url, entity);
        long transactionId;
        try {
            transactionId = restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            entity,
                            WalletTransactionResponseDto.class)
                    .getBody().getWalletTransactionId();
            logger.info("discountFunds: End exchanging POST {} ended successfully and wallet_transaction_id {}", url, transactionId);
            return Either.right(transactionId);
        } catch (HttpStatusCodeException hsce) {
            logger.error("moveFunds: Error exchanging POST {} - httpStatus: {} - response: {}", url, hsce.getStatusCode(), hsce.getResponseBodyAsString());
            return Either.left(new TransferError(hsce.getStatusCode(), hsce.getResponseBodyAsString()));
        } catch (Exception e) {
            logger.error("moveFunds: Error exchanging POST {} - message: {}", url, e.getMessage());
            return Either.left(new TransferError(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage()));
        }
    }

    private String buildBalanceUrl(long userId) {
        return UriComponentsBuilder.fromHttpUrl(config.getWalletApiUrl() + config.getBalancePath())
                .buildAndExpand(userId)
                .toUriString();
    }

    private String buildTransactionUrl() {
        return UriComponentsBuilder.fromHttpUrl(config.getWalletApiUrl() + config.getTransactionPath())
                .toUriString();
    }

    private HttpEntity<?> buildBalanceEntity() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(headers);
    }

    private HttpEntity<?> buildTransactionEntity(FundsRequestDto fundsRequestDto) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(fundsRequestDto, headers);
    }

}
