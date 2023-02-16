package com.accenture.paymentModule.controller;

import com.accenture.paymentModule.dtos.TransactionDTO;
import com.accenture.paymentModule.entity.PaymentType;
import com.accenture.paymentModule.entity.Transaction;
import com.accenture.paymentModule.service.ITransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    @Autowired
    @Qualifier("transactionServiceFeign")
    private ITransactionService transactionService;


    @GetMapping("/list")
    public List<Transaction> userList() {
        return transactionService.findAll();
    }

    @GetMapping("/id")
    public Transaction getByTransactionId(Long id) {
        return transactionService.findById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<Object> createTransaction(@RequestBody TransactionDTO transactionDTO) throws Exception {
        try {
            if (transactionDTO.getScheduledDate() == null ||
                    transactionDTO.getScheduledDate().toString().trim().isEmpty()) {
                LocalDate today = LocalDate.now();
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String formattedDate = today.format(dateTimeFormatter);
                transactionDTO.setScheduledDate(LocalDate.parse(formattedDate, dateTimeFormatter));
            }
            if (transactionDTO.getPaymentType().equals(PaymentType.ECHEQ)
                    || transactionDTO.getPaymentType().equals(PaymentType.CARD)
                    || transactionDTO.getScheduledDate().isAfter(LocalDate.now())) {
                Transaction transaction = transactionService.createSpecialTransaction(transactionDTO);
                transactionService.transactionScheduled();
//                String message = senderController.convertAndSend(transaction);
                return new ResponseEntity<>("The transaction will be processed", HttpStatus.OK);
            } else {
                Transaction transaction = transactionService.generateTransaction(transactionDTO);
                return new ResponseEntity<>(transaction, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
        }
    }
}
