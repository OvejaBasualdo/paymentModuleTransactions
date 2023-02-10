package com.accenture.paymentModule.clients;


import com.accenture.paymentModule.model.Account;
import com.accenture.paymentModule.model.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "microservice-payments-user", url = "localhost:8001")
public interface userClient {
    @GetMapping("/list")
    public List<User> userList();
    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id);
    @GetMapping("/user/accounts/{idAccount}")
    public List<Account> getUserByUserId(@PathVariable Long idAccount);
    @PutMapping(value = "/deleteUserAccount", consumes = "application/json", produces = "application/json")
    public ResponseEntity<Object> deleteUserAccounts(@RequestBody User user, @RequestParam Long idAccount) throws Exception;

    @PutMapping("/editUser")
    public ResponseEntity<Object> editUser(@RequestParam Long id, @RequestBody User user);
}

