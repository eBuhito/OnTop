package com.ontop.transfer.core.application.usecase;

import com.ontop.transfer.core.application.exception.InsufficientFundsException;
import com.ontop.transfer.core.application.port.inbound.WithdrawInPort;
import com.ontop.transfer.core.application.port.outbound.PaymentOutPort;
import com.ontop.transfer.core.application.port.outbound.TransactionOutPort;
import com.ontop.transfer.core.application.port.outbound.WalletOutPort;
import com.ontop.transfer.core.application.port.outbound.WalletTransactionOutPort;
import com.ontop.transfer.core.domain.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class WithdrawUseCase implements WithdrawInPort {
    private final WalletOutPort walletService;
    private final WalletTransactionOutPort walletTransactionService;
    private final TransactionOutPort transactionService;
    private final PaymentOutPort paymentService;
    @Value(value = "${withdraw.fee}")
    private double withdrawFee;
    @Value(value = "${source.type}")
    private String sourceType;
    @Value(value = "${source.name}")
    private String sourceName;
    @Value(value = "${source.account.number}")
    private String sourceAccountNumber;
    @Value(value = "${source.account.currency}")
    private String sourceAccountCurrency;
    @Value(value = "${source.account.routing-number}")
    private String sourceAccountRoutingNumber;

    public WithdrawUseCase(WalletOutPort walletService, WalletTransactionOutPort walletTransactionService, TransactionOutPort transactionService, PaymentOutPort paymentService) {
        this.walletService = walletService;
        this.walletTransactionService = walletTransactionService;
        this.transactionService = transactionService;
        this.paymentService = paymentService;
    }

    @Override
    public WithdrawResponse withdraw(WithdrawRequest withdrawRequest) {
//        1- validar usuario
//        2- validar fondos
//        3- buscar cuenta destino
//        4- descontar fondos de la wallet
//        5- calcular fee
//        6- registrar transaccion de extraccion
//        7- solicitar transferencia
//        8- actualizar estad de la transaccion

        validate(withdrawRequest);
        DestinationBankAccount destinationBankAccountDetails = getDestinationBankAccountDetails();
        long discountTransactionId = discountFundsFromWallet(withdrawRequest.getUserId(), withdrawRequest.getAmount());
        double feeAmount = calculateFee(withdrawRequest.getAmount());
        long transactionId = registerTransaction(buildTransaction(withdrawRequest.getUserId(), withdrawRequest, feeAmount, discountTransactionId, "INITIAL"));
        PaymentInfo paymentInfo = executeTransfer(buildSource(), buildDestination(destinationBankAccountDetails), withdrawRequest.getAmount() - feeAmount);
        updateTransaction(transactionId, "IN PROGRESS");

        return WithdrawResponse.from(withdrawRequest, feeAmount);
    }

    private void updateTransaction(long transactionId, String status) {
        transactionService.updateTransaction(transactionId, status);
    }

    private BankAccount buildDestination(DestinationBankAccount destinationBankAccountDetails) {
        return new BankAccount(null, destinationBankAccountDetails.getName(), buildDestinationAccount(destinationBankAccountDetails));
    }

    private Account buildDestinationAccount(DestinationBankAccount destinationBankAccountDetails) {
        return new Account(destinationBankAccountDetails.getAccountNumber(), destinationBankAccountDetails.getCurrency(), destinationBankAccountDetails.getRoutingNumber());
    }

    private BankAccount buildSource() {
        return new BankAccount(sourceType, sourceName, buildSourceAccount());
    }

    private Account buildSourceAccount() {
        return new Account(sourceAccountNumber, sourceAccountCurrency, sourceAccountRoutingNumber);
    }

    private Transaction buildTransaction(long userId, WithdrawRequest withdrawRequest, double feeAmount, long discountTransactionId, String status) {
        return new Transaction(userId, withdrawRequest.getAmount(), feeAmount, status, null, discountTransactionId);
    }

    private PaymentInfo executeTransfer(BankAccount source, BankAccount destination, double netAmount) {
        return paymentService.executePayment(source, destination, netAmount);
    }

    private long registerTransaction(Transaction transaction) {
        return transactionService.registerTransaction(transaction);
    }

    private double calculateFee(double amount) {
        return amount * withdrawFee;
    }

    private long discountFundsFromWallet(long userId, double amount) {
        return walletTransactionService.discountFunds(userId, amount);
    }

    private DestinationBankAccount getDestinationBankAccountDetails() {
        return new DestinationBankAccount(
                "El ZORRO",
                "211927207",
                "1885226711",
                "USD"
        );
    }

    private void validate(WithdrawRequest withdrawRequest) {
        validateUser(withdrawRequest.getUserId());
        validateFunds(withdrawRequest.getUserId(), withdrawRequest.getAmount());
    }

    private void validateFunds(long userId, double amount) {
        Balance balance = walletService.getBalance(userId);
        if (balance.getBalance() < amount) {
            throw new InsufficientFundsException(userId);
        }
    }

    private void validateUser(long userId) {
//        TODO - validar que el usuario existe y esta habilitado
    }
}
