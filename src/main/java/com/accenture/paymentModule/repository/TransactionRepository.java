package com.accenture.paymentModule.repository;

import com.accenture.paymentModule.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByAmountGreaterThan(BigDecimal amount);
    Optional<Transaction> findByFromAccountOrderByTransactionDateDesc(String fromAccount);
    Optional<Transaction> findByTransactionDate(LocalDateTime transactionDate);

}
