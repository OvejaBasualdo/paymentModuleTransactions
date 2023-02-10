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
import java.util.List;

@RestController
@RequestMapping("/api/transaction")
public class TransactionController {

    @Autowired
    @Qualifier("transactionServiceFeign")
    private ITransactionService transactionService;

    @Autowired
    private SenderController senderController;

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
            if (transactionDTO.getPaymentType().equals(PaymentType.ECHEQ)
                    || transactionDTO.getPaymentType().equals(PaymentType.CARD)
                    || transactionDTO.getTransactionDate().toLocalDate().isAfter(LocalDate.now())) {
                //ENDPOINT DE CTA ORIGEN ASI ACUTALIZA RAPIDO
                //TRANSACCION SAVE = GENERA JSON TRANSACTION
                String message = senderController.convertAndSend(transactionDTO);
          //      senderController.send("This is a test transaction");
                Transaction transaction =transactionService.createSpecialTransaction(transactionDTO);
                return new ResponseEntity<>(message, HttpStatus.OK);
            } else {
                Transaction transaction = transactionService.generateTransaction(transactionDTO);
                return new ResponseEntity<>(transaction, HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.OK);
        }
    }
}
