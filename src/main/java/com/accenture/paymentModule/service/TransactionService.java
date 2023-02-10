package com.accenture.paymentModule.service;

import com.accenture.paymentModule.clients.accountClient;
import com.accenture.paymentModule.dtos.TransactionDTO;
import com.accenture.paymentModule.entity.Transaction;
import com.accenture.paymentModule.exceptions.ElementNotFoundException;
import com.accenture.paymentModule.exceptions.EmptyDataException;
import com.accenture.paymentModule.model.Account;
import com.accenture.paymentModule.repository.TransactionRepository;
import com.accenture.paymentModule.utils.TransactionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service("transactionServiceFeign")
@Primary
public class TransactionService implements ITransactionService {


    @Autowired
    accountClient accountClientsRestFeign;
    @Autowired
    TransactionRepository transactionRepository;

    @Override
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Transaction findById(Long id) {
        return transactionRepository.findById(id).orElse(null);
    }

    @Override
    public Transaction generateTransaction(TransactionDTO transactionDTO) throws Exception {
        Account account = accountClientsRestFeign.getByAccountNumber(transactionDTO.getFromAccount());
        if (account == null) {
            throw new ElementNotFoundException("Origin Account Not Found");
        }
        if (accountClientsRestFeign.getByAccountNumber(transactionDTO.getToAccount()) == null) {
            throw new ElementNotFoundException("Destination Account Not Found");
        }
        if (TransactionUtils.checkingEmptyData(transactionDTO)) {
            throw new EmptyDataException("Empty field/s");
        }
        if (!(transactionDTO.getToAccount().trim().length() == 10) || !(transactionDTO.getFromAccount().trim().length() == 10)) {
            throw new Exception("Error in Account origin/destination: must be 10 digits");
        }
        if (TransactionUtils.verifyNumber(transactionDTO.getFromAccount().trim())
                || TransactionUtils.verifyNumber(transactionDTO.getToAccount().trim())) {
            throw new Exception("Error in Account origin/destination: must be only numbers");
        }
        if (transactionDTO.getToAccount().trim().equals(transactionDTO.getFromAccount().trim())) {
            throw new Exception("Error in Account origin/destination: they must be different");
        }
        if (TransactionUtils.isAmountBiggerThanZero(transactionDTO)) {
            throw new Exception("Error in the Amount to transfer: they must be greater than 0");
        }
        if (TransactionUtils.isAmountBiggerThan(transactionDTO, account.getBalance())) {
            throw new Exception("Error in the Amount to transfer: they must be greater than " + account.getBalance());
        }
        if (TransactionUtils.haveTwoDecimal(transactionDTO.getAmount())) {
            throw new Exception("Error in the Amount to transfer: they must be greater than 0");
        }
        if (TransactionUtils.dateBefore(transactionDTO.getTransactionDate())) {
            throw new Exception("Error in the Date: Must be now or later");
        }
        if (TransactionUtils.checkFormatDate(transactionDTO.getTransactionDate().toLocalDate())) {
            throw new Exception("Error with format date, remember DD/HH/YYYY");
        }
        Transaction transactionOk = new Transaction(transactionDTO);
        accountClientsRestFeign.updateBalance(transactionDTO);
        transactionRepository.save(transactionOk);
        return transactionOk;
    }

    @Override
    public Transaction createSpecialTransaction(TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction(transactionDTO);
        accountClientsRestFeign.updateBalanceAccountSender(transactionDTO);
        //logger.info("MSTransaction RestTemplated: created Special Transaction");
        return transactionRepository.save(transaction);
    }
}
