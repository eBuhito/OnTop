package com.ontop.transfer.core.application.usecase;

import com.ontop.transfer.core.application.exception.BankAccountNotFoundException;
import com.ontop.transfer.core.application.exception.BusinessException;
import com.ontop.transfer.core.application.port.outbound.BankOutPort;
import com.ontop.transfer.core.application.port.outbound.PaymentOutPort;
import com.ontop.transfer.core.application.port.outbound.TransactionOutPort;
import com.ontop.transfer.core.application.port.outbound.WalletOutPort;
import com.ontop.transfer.core.application.usecase.config.BankConfig;
import com.ontop.transfer.core.domain.model.*;
import io.vavr.control.Either;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WithdrawRequestUseCaseTest {

    @Mock
    private WalletOutPort walletService;
    @Mock
    private BankOutPort bankService;
    @Mock
    private TransactionOutPort transactionService;
    @Mock
    private PaymentOutPort paymentService;
    @Mock
    private BankConfig config;

    @InjectMocks
    private WithdrawUseCase withdrawUseCase;
    private long userId;
    private Either<TransferError, Balance> balance;
    private String bankAccount;
    private double amount;
    private double grossAmount;
    private BankAccount source;
    private BankAccount destination;

    @BeforeEach
    void setUp() {
        userId = 333;
        bankAccount = "22";
        amount = 100.0;
        grossAmount = 110.0;
        when(config.getPercentageFee()).thenReturn(0.1);
    }

    @Test
    void successWithdraw() {
        when(config.getSourceAccountNumber()).thenReturn("22");
        when(config.getSourceType()).thenReturn("FUENTE");
        when(config.getSourceName()).thenReturn("ORIGEN");
        when(config.getSourceAccountRoutingNumber()).thenReturn("4444");
        when(config.getSourceAccountCurrency()).thenReturn("USD");
        balance = Either.right(new Balance(userId, 999.99));
        when(walletService.getBalance(userId)).thenReturn(balance);
        DestinationBankAccount destinationBankAccount = DestinationBankAccount.builder()
                .accountNumber("7777777")
                .currency("USD")
                .name("DESTINO")
                .routingNumber("666666")
                .build();
        when(bankService.getDestinationBankAccountDetails(bankAccount)).thenReturn(destinationBankAccount);
        Either<TransferError, Long> discountTransactionId = Either.right(11111111111L);
        when(walletService.moveFunds(userId, grossAmount * -1)).thenReturn(discountTransactionId);
        Transaction transaction = Transaction.builder()
                .userId(userId)
                .discountTransactionId(discountTransactionId.get())
                .amount(amount * -1)
                .type(TransactionType.TRANSFER)
                .status(TransferStatus.IN_PROGRESS)
                .currency("USD")
                .build();
        Either<TransferError, Long> entityResult = Either.right(1L);
        when(transactionService.registerTransaction(transaction)).thenReturn(entityResult);
        Account sourceAccount = Account.builder()
                .accountNumber("22")
                .routingNumber("4444")
                .currency("USD")
                .build();
        source = BankAccount.builder()
                .account(sourceAccount)
                .type("FUENTE")
                .name("ORIGEN")
                .build();
        Account destAccount = Account.builder()
                .currency("USD")
                .accountNumber("7777777")
                .routingNumber("666666")
                .build();
        destination = BankAccount.builder()
                .name("DESTINO")
                .account(destAccount)
                .build();
        Either<TransferError, PaymentInfo> paymentInfo = Either.right(new PaymentInfo(amount, "algun-token-id"));
        when(paymentService.executePayment(source, destination, amount)).thenReturn(paymentInfo);
        WithdrawRequest withdrawRequest = WithdrawRequest.builder()
                .userId(userId)
                .netAmount(amount)
                .destinationBankAccountId(bankAccount)
                .build();

        WithdrawResponse result = withdrawUseCase.withdraw(withdrawRequest);

        assertEquals(100.0, result.getAmount());
        assertEquals(10.0, result.getFeeAmount());
        assertEquals(333L, result.getUserId());
        assertEquals("22", result.getDestinationBankAccountId());
        verify(transactionService, times(1)).registerTransaction(any());
    }

    @Test
    void failWithdrawAndRefundFunds() {
        when(config.getSourceAccountNumber()).thenReturn("22");
        when(config.getSourceType()).thenReturn("FUENTE");
        when(config.getSourceName()).thenReturn("ORIGEN");
        when(config.getSourceAccountRoutingNumber()).thenReturn("4444");
        when(config.getSourceAccountCurrency()).thenReturn("USD");
        balance = Either.right(new Balance(userId, 999.99));
        when(walletService.getBalance(userId)).thenReturn(balance);
        DestinationBankAccount destinationBankAccount = DestinationBankAccount.builder()
                .accountNumber("7777777")
                .currency("USD")
                .name("DESTINO")
                .routingNumber("666666")
                .build();
        when(bankService.getDestinationBankAccountDetails(bankAccount)).thenReturn(destinationBankAccount);
        Either<TransferError, Long> discountTransactionId = Either.right(11111111111L);
        when(walletService.moveFunds(userId, grossAmount * -1)).thenReturn(discountTransactionId);
        Transaction transaction = Transaction.builder()
                .userId(userId)
                .discountTransactionId(discountTransactionId.get())
                .amount(amount * -1)
                .type(TransactionType.TRANSFER)
                .status(TransferStatus.IN_PROGRESS)
                .currency("USD")
                .build();
        Either<TransferError, Long> entityResult = Either.right(1L);
        when(transactionService.registerTransaction(transaction)).thenReturn(entityResult);
        Account sourceAccount = Account.builder()
                .accountNumber("22")
                .routingNumber("4444")
                .currency("USD")
                .build();
        source = BankAccount.builder()
                .account(sourceAccount)
                .type("FUENTE")
                .name("ORIGEN")
                .build();
        Account destAccount = Account.builder()
                .currency("USD")
                .accountNumber("7777777")
                .routingNumber("666666")
                .build();
        destination = BankAccount.builder()
                .name("DESTINO")
                .account(destAccount)
                .build();
        Either<TransferError, PaymentInfo> transferError = Either.left(new TransferError(
                HttpStatus.BAD_REQUEST, "no se realizo la transaccion"));
        when(paymentService.executePayment(source, destination, amount)).thenReturn(transferError);
        Either<TransferError, Long> fundsResult = Either.right(2L);
        when(walletService.moveFunds(userId, grossAmount)).thenReturn(fundsResult);
        transaction = Transaction.builder()
                .userId(userId)
                .discountTransactionId(fundsResult.get())
                .amount(amount)
                .type(TransactionType.REFUND)
                .status(TransferStatus.REFUNDED)
                .currency("USD")
                .build();
        when(transactionService.registerTransaction(transaction)).thenReturn(entityResult);
        WithdrawRequest withdrawRequest = WithdrawRequest.builder()
                .userId(userId)
                .netAmount(amount)
                .destinationBankAccountId(bankAccount)
                .build();

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            withdrawUseCase.withdraw(withdrawRequest);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getError().getCode());
        assertEquals("no se realizo la transaccion", exception.getError().getError());
        verify(transactionService, times(2)).registerTransaction(any());
    }

    @Test
    void failRegisterTransaction() {
        balance = Either.right(new Balance(userId, 999.99));
        when(walletService.getBalance(userId)).thenReturn(balance);
        DestinationBankAccount destinationBankAccount = DestinationBankAccount.builder()
                .accountNumber("7777777")
                .currency("USD")
                .name("DESTINO")
                .routingNumber("666666")
                .build();
        when(bankService.getDestinationBankAccountDetails(bankAccount)).thenReturn(destinationBankAccount);
        Either<TransferError, Long> discountTransactionId = Either.right(11111111111L);
        when(walletService.moveFunds(userId, grossAmount * -1)).thenReturn(discountTransactionId);
        Transaction transaction = Transaction.builder()
                .userId(userId)
                .discountTransactionId(discountTransactionId.get())
                .amount(amount * -1)
                .type(TransactionType.TRANSFER)
                .status(TransferStatus.IN_PROGRESS)
                .currency("USD")
                .build();
        Either<TransferError, Long> transferError = Either.left(new TransferError(
                HttpStatus.UNPROCESSABLE_ENTITY, "error en la DB"));
        when(transactionService.registerTransaction(transaction)).thenReturn(transferError);
        WithdrawRequest withdrawRequest = WithdrawRequest.builder()
                .userId(userId)
                .netAmount(amount)
                .destinationBankAccountId(bankAccount)
                .build();

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            withdrawUseCase.withdraw(withdrawRequest);
        });

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.getError().getCode());
        assertEquals("error en la DB", exception.getError().getError());
        verify(transactionService, times(1)).registerTransaction(any());
        verifyNoInteractions(paymentService);
    }

    @Test
    void failOnDiscountFundsFromWallet() {
        balance = Either.right(new Balance(userId, 999.99));
        when(walletService.getBalance(userId)).thenReturn(balance);
        DestinationBankAccount destinationBankAccount = DestinationBankAccount.builder()
                .accountNumber("7777777")
                .currency("USD")
                .name("DESTINO")
                .routingNumber("666666")
                .build();
        when(bankService.getDestinationBankAccountDetails(bankAccount)).thenReturn(destinationBankAccount);
        Either<TransferError, Long> transferError = Either.left(new TransferError(
                HttpStatus.NOT_FOUND,
                "Usuario no existe"
        ));
        when(walletService.moveFunds(userId, grossAmount * -1)).thenReturn(transferError);
        WithdrawRequest withdrawRequest = WithdrawRequest.builder()
                .userId(userId)
                .netAmount(amount)
                .destinationBankAccountId(bankAccount)
                .build();

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            withdrawUseCase.withdraw(withdrawRequest);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getError().getCode());
        assertEquals("Usuario no existe", exception.getError().getError());
        verifyNoInteractions(transactionService);
        verifyNoInteractions(paymentService);
    }

    @Test
    void failWhenDestinationBankDoesntExists() {
        balance = Either.right(new Balance(userId, 999.99));
        when(walletService.getBalance(userId)).thenReturn(balance);
        when(bankService.getDestinationBankAccountDetails(bankAccount))
                .thenThrow(new BankAccountNotFoundException());
        WithdrawRequest withdrawRequest = WithdrawRequest.builder()
                .userId(userId)
                .netAmount(amount)
                .destinationBankAccountId(bankAccount)
                .build();

        BankAccountNotFoundException exception = assertThrows(BankAccountNotFoundException.class, () -> {
            withdrawUseCase.withdraw(withdrawRequest);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getError().getCode());
        assertEquals("Bank account not found", exception.getError().getError());
        verifyNoMoreInteractions(walletService);
        verifyNoInteractions(transactionService);
        verifyNoInteractions(paymentService);
    }

    @Test
    void failWhenBalanceIsNotEnough() {
        balance = Either.right(new Balance(userId, 1));
        when(walletService.getBalance(userId)).thenReturn(balance);
        WithdrawRequest withdrawRequest = WithdrawRequest.builder()
                .userId(userId)
                .netAmount(amount)
                .destinationBankAccountId(bankAccount)
                .build();

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            withdrawUseCase.withdraw(withdrawRequest);
        });

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, exception.getError().getCode());
        assertEquals("Insufficient funds for user " + userId, exception.getError().getError());
        verifyNoMoreInteractions(walletService);
        verifyNoInteractions(transactionService);
        verifyNoInteractions(paymentService);
    }

    @Test
    void failWhenGetBalanceReturnAnerror() {
        Either<TransferError, Balance> transferError = Either.left(new TransferError(
                HttpStatus.BAD_REQUEST,
                "error al buscar el balance"
        ));
        when(walletService.getBalance(userId)).thenReturn(transferError);
        WithdrawRequest withdrawRequest = WithdrawRequest.builder()
                .userId(userId)
                .netAmount(amount)
                .destinationBankAccountId(bankAccount)
                .build();

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            withdrawUseCase.withdraw(withdrawRequest);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getError().getCode());
        assertEquals("error al buscar el balance", exception.getError().getError());
        verifyNoMoreInteractions(walletService);
        verifyNoInteractions(transactionService);
        verifyNoInteractions(paymentService);
    }
}