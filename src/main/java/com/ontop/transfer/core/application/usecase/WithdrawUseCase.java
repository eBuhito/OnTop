package com.ontop.transfer.core.application.usecase;

import com.ontop.transfer.core.application.exception.BusinessException;
import com.ontop.transfer.core.application.port.inbound.WithdrawInPort;
import com.ontop.transfer.core.application.port.outbound.BankOutPort;
import com.ontop.transfer.core.application.port.outbound.PaymentOutPort;
import com.ontop.transfer.core.application.port.outbound.TransactionOutPort;
import com.ontop.transfer.core.application.port.outbound.WalletOutPort;
import com.ontop.transfer.core.application.usecase.config.BankConfig;
import com.ontop.transfer.core.domain.model.*;
import io.vavr.control.Either;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class WithdrawUseCase implements WithdrawInPort {
    private final WalletOutPort walletService;
    private final BankOutPort bankService;
    private final TransactionOutPort transactionService;
    private final PaymentOutPort paymentService;
    private final BankConfig config;

    public WithdrawUseCase(
            WalletOutPort walletService, BankOutPort bankService, TransactionOutPort transactionService, PaymentOutPort paymentService, BankConfig bankConfig) {
        this.walletService = walletService;
        this.bankService = bankService;
        this.transactionService = transactionService;
        this.paymentService = paymentService;
        this.config = bankConfig;
    }

    @Override
    public WithdrawResponse withdraw(WithdrawRequest withdrawRequest) {
        calculateGrossAmount(withdrawRequest);
        validate(withdrawRequest);
        DestinationBankAccount destinationBankAccountDetails = getDestinationBankAccountDetails(withdrawRequest.getDestinationBankAccountId());
        long discountTransactionId = discountFundsFromWallet(withdrawRequest.getUserId(), withdrawRequest.getGrossAmount());
        long transactionId = registerTransaction(
                buildInitTransferTransaction(
                        withdrawRequest,
                        destinationBankAccountDetails.getCurrency(),
                        discountTransactionId));
        Either<TransferError, PaymentInfo> transferResult = executeTransfer(
                buildSource(),
                buildDestination(destinationBankAccountDetails),
                withdrawRequest.getNetAmount());
        handleResult(withdrawRequest, destinationBankAccountDetails, transactionId, transferResult);

        return WithdrawResponse.from(withdrawRequest);
    }

    private void handleResult(
            WithdrawRequest withdraw, DestinationBankAccount destinationBankAccountDetails, long transactionId, Either<TransferError, PaymentInfo> transferResult) {
        if (transferResult.isLeft()) {
            updateTransaction(transactionId);
            long refundedTransactionId = topUpFundsFromWallet(withdraw.getUserId(), withdraw.getGrossAmount());
            registerTransaction(
                    buildRefundTransferTransaction(
                            withdraw,
                            destinationBankAccountDetails.getCurrency(),
                            refundedTransactionId));

            throw new BusinessException(transferResult.getLeft());
        } else {
            updateTransaction(transactionId, transferResult.get().getId());
        }
    }

    private void updateTransaction(long transactionId) {
        transactionService.updateTransaction(transactionId, TransferStatus.FAILED, null);
    }

    private void calculateGrossAmount(WithdrawRequest withdraw) {
        withdraw.setGrossAmount(withdraw.getNetAmount() + getFee(withdraw.getNetAmount()));
    }

    private double getFee(Double netAmount) {
        return netAmount * config.getPercentageFee();
    }

    private void updateTransaction(long transactionId, String paymentId) {
        transactionService.updateTransaction(transactionId, TransferStatus.COMPLETED, paymentId);
    }

    private BankAccount buildDestination(DestinationBankAccount destinationBankAccountDetails) {
        return new BankAccount(null, destinationBankAccountDetails.getName(), buildDestinationAccount(destinationBankAccountDetails));
    }

    private Account buildDestinationAccount(DestinationBankAccount destinationBankAccountDetails) {
        return new Account(destinationBankAccountDetails.getAccountNumber(), destinationBankAccountDetails.getCurrency(), destinationBankAccountDetails.getRoutingNumber());
    }

    private BankAccount buildSource() {
        return new BankAccount(config.getSourceType(), config.getSourceName(), buildSourceAccount());
    }

    private Account buildSourceAccount() {
        return new Account(
                config.getSourceAccountNumber(),
                config.getSourceAccountCurrency(),
                config.getSourceAccountRoutingNumber());
    }

    private Transaction buildInitTransferTransaction(WithdrawRequest withdraw, String currency, long discountTransactionId) {
        return new Transaction(
                withdraw.getUserId(),
                withdraw.getNetAmount() * -1,
                currency,
                TransactionType.TRANSFER,
                TransferStatus.IN_PROGRESS,
                null,
                discountTransactionId);
    }

    private Transaction buildRefundTransferTransaction(WithdrawRequest withdrawRequest, String currency, long refundedTransactionId) {
        return new Transaction(
                withdrawRequest.getUserId(),
                withdrawRequest.getNetAmount(),
                currency,
                TransactionType.REFUND,
                TransferStatus.REFUNDED,
                null,
                refundedTransactionId);
    }

    private Either<TransferError, PaymentInfo> executeTransfer(BankAccount source, BankAccount destination, double netAmount) {
        return paymentService.executePayment(source, destination, netAmount);
    }

    private long registerTransaction(Transaction transaction) {
        Either<TransferError, Long> result = transactionService.registerTransaction(transaction);
        if (result.isLeft()) throw new BusinessException(result.getLeft());
        return result.get();
    }

    private long discountFundsFromWallet(long userId, double amount) {
        Either<TransferError, Long> fundsResult = walletService.moveFunds(userId, amount * -1);
        if (fundsResult.isLeft()) throw new BusinessException(fundsResult.getLeft());
        return fundsResult.get();
    }

    private long topUpFundsFromWallet(long userId, double amount) {
        Either<TransferError, Long> fundsResult = walletService.moveFunds(userId, amount);
        if (fundsResult.isLeft()) throw new BusinessException(fundsResult.getLeft());
        return fundsResult.get();
    }

    private DestinationBankAccount getDestinationBankAccountDetails(String bankAccount) {
        return bankService.getDestinationBankAccountDetails(bankAccount);
    }

    private void validate(WithdrawRequest withdrawRequest) {
        validateUser(withdrawRequest.getUserId());
        validateFunds(withdrawRequest.getUserId(), withdrawRequest.getGrossAmount());
    }

    private void validateFunds(long userId, double amount) {
        Either<TransferError, Balance> balance = walletService.getBalance(userId);
        if (balance.isLeft()) throw new BusinessException(balance.getLeft());
        if (balance.get().getBalance() < amount) {
            throw new BusinessException(new TransferError(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Insufficient funds for user " + userId));
        }
    }

    private void validateUser(long userId) {
//        TODO - validar que el usuario existe y esta habilitado
    }
}
