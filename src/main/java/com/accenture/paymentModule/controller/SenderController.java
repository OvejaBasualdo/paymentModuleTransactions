package com.accenture.paymentModule.controller;

import com.accenture.paymentModule.dtos.TransactionDTO;
import com.accenture.paymentModule.entity.Transaction;
import com.google.gson.Gson;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.ObjectMessage;
import jakarta.jms.Session;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/messaging")
public class SenderController {

    @Autowired
    JmsTemplate jmsTemplate;

    @GetMapping("/send/{message}")
    public String send(@PathVariable("message") String message) {
        jmsTemplate.send("transactionDelayed", new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {

                ObjectMessage object = session.createObjectMessage(message);
                return object;
            }
        });
        return message;
    }

    public String convertAndSend(Transaction transactionDTO) {
        jmsTemplate.send("transactionDelayed", new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", transactionDTO.getId());
                jsonObject.put("amount", transactionDTO.getAmount());
                jsonObject.put("fromAccount", transactionDTO.getFromAccount());
                jsonObject.put("toAccount", transactionDTO.getToAccount());
                jsonObject.put("paymentType", transactionDTO.getPaymentType());
                jsonObject.put("boolean", false);
                System.out.println(transactionDTO.getScheduledDate());
                jsonObject.put("scheduledDate", transactionDTO.getScheduledDate().toString());
                ObjectMessage object = session.createObjectMessage(jsonObject.toString());
                System.out.println(object);
                return object;
            }
        });

        return "TRANSACTIONDTO to string ";
    }
}