package com.accenture.paymentModule.clients;


import com.accenture.paymentModule.dtos.TransactionDTO;
import com.accenture.paymentModule.model.Account;
import com.accenture.paymentModule.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "microservice-payments-account", url = "localhost:8002")
public interface accountClient {
    @GetMapping("/api/accounts/list")
    public List<Account> getListAccounts();
    @GetMapping("/api/accounts/list/id/{id}")
    public Account getById(@PathVariable Long id);
    @GetMapping("/api/accounts/id/{idAccount}")
    public List<Account> getByUserId(@PathVariable Long idAccount);
    @GetMapping("/api/accounts/list/accountNumber/{accountNumber}")
    public Account getByAccountNumber(@PathVariable String accountNumber);

    @GetMapping("/api/accounts/list/cbu/{cbu}")
    public ResponseEntity<Object> getByCbu(@PathVariable String cbu);

    @PostMapping("/api/accounts/createAccount")
    public Account createAccount(@RequestBody User user);
    @PutMapping(value = "/api/accounts/deleteAccount", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> deleteAccount(@RequestBody User user, @RequestParam Long idAccount);
    @PostMapping("/api/accounts/updateBalance")
    public void updateBalance(@RequestBody TransactionDTO transactionInfoDTO);
    @PostMapping("/api/accounts/updateBalanceAccountSender")
    public void updateBalanceAccountSender(@RequestBody TransactionDTO transactionInfoDTO);

}
