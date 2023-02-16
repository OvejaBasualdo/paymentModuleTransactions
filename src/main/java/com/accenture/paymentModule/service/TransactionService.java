package com.accenture.paymentModule.service;

import com.accenture.paymentModule.clients.accountClient;
import com.accenture.paymentModule.controller.SenderController;
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


import java.time.LocalDate;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Service("transactionServiceFeign")
@Primary
public class TransactionService implements ITransactionService {


    @Autowired
    accountClient accountClientsRestFeign;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    private SenderController senderController;

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
        if (TransactionUtils.dateBefore(transactionDTO.getScheduledDate())) {
            throw new Exception("Error in the Date: Must be now or later");
        }
        if (TransactionUtils.checkFormatDate(transactionDTO.getScheduledDate())) {
            throw new Exception("Error with format date, remember DD/HH/YYYY");
        }
        Transaction transactionOk = new Transaction(transactionDTO);
        accountClientsRestFeign.updateBalance(transactionDTO);
        transactionOk.setWasProcessed(Boolean.TRUE);
        transactionRepository.save(transactionOk);
        return transactionOk;
    }

    @Override
    public Transaction createSpecialTransaction(TransactionDTO transactionDTO) {
        Transaction transaction = new Transaction(transactionDTO);
        return transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> findByScheduledDateAndWasProcessedFalse() {
        List<Transaction> todayList = transactionRepository.findByScheduledDateAndWasProcessedFalse(LocalDate.now());
        if (todayList.isEmpty() || todayList == null) {
            return null;
        } else {
            return todayList;
        }
    }

    public void transactionScheduled() {
        Timer timer = new Timer();
        TimerTask transactionToday = new TimerTask() {
            @Override
            public void run() {
                List<Transaction> list = findByScheduledDateAndWasProcessedFalse();
                if (list.isEmpty() || list == null) {
                    System.out.println("No transaction today");
                } else {
                    System.out.println("Transactions list = " + list.size());
                    for (Transaction aux : list) {
                        senderController.convertAndSend(aux);
                        aux.setWasProcessed(Boolean.TRUE);
                        transactionRepository.save(aux);
                        System.out.println("Transaction n° " + aux.getId() + " was processed");
                    }
                }
            }
        };
        timer.schedule(transactionToday, 0, 60000);
    }


}
