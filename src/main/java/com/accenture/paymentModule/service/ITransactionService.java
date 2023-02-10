package com.accenture.paymentModule.service;

import com.accenture.paymentModule.dtos.TransactionDTO;
import com.accenture.paymentModule.entity.Transaction;

import java.util.List;

public interface ITransactionService {
    public List<Transaction> findAll();
    public Transaction findById(Long id);
    public Transaction generateTransaction(TransactionDTO transactionDTO) throws Exception;
    public Transaction createSpecialTransaction(TransactionDTO transactionDTO);

}
