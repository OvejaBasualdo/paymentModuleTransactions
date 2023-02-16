package com.accenture.paymentModule.entity;

import com.accenture.paymentModule.dtos.TransactionDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    private BigDecimal amount;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime transactionDate;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate scheduledDate;
    private TransactionType transactionType;
    private PaymentType paymentType;
    private String fromAccount;
    private String toAccount;
    private String description;
    private Boolean wasProcessed;

    public Transaction(TransactionDTO transactionDTO) {
        this.amount = transactionDTO.getAmount();
        this.transactionDate = LocalDateTime.now();
        this.scheduledDate = transactionDTO.getScheduledDate();
        this.transactionType = transactionDTO.getTransactionType();
        this.paymentType = transactionDTO.getPaymentType();
        this.fromAccount = transactionDTO.getFromAccount();
        this.toAccount = transactionDTO.getToAccount();
        this.description = transactionDTO.getDescription();
        this.wasProcessed = Boolean.FALSE;
    }

    public Boolean getWasProcessed() {
        return wasProcessed;
    }

    public void setWasProcessed(Boolean wasProcessed) {
        this.wasProcessed = wasProcessed;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Transaction() {
    }
}
