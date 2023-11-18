package com.example.wallet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.Uuid;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper; //helps in transforming json object to string1

    public String initiateTransaction(String sender, String receiver, String purpose, Double amount) throws JsonProcessingException {

        Transaction transaction = Transaction.builder()
                .sender(sender)
                .receiver(receiver)
                .purpose(purpose)
                .transactionId(Uuid.randomUuid().toString())
                .transactionStatus(TransactionStatus.PENDING)
                .amount(amount)
                .build();

        transactionRepository.save(transaction);

        //publish the event after the transaction which will be consumed by the consumers
        JSONObject json = new JSONObject();
        json.put("sender", sender);
        json.put("receiver", receiver);
        json.put("amount", amount);
        json.put("transactionId", transaction.getTransactionId());

        //Now to publish the message
        kafkaTemplate.send(CommonConstants.TRANSACTION_CREATION_TOPIC, objectMapper.writeValueAsString(json));

        return transaction.getTransactionId();


    }
}
