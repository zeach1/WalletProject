package com.example.wallet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.Uuid;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
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

    private static Logger logger = LoggerFactory.getLogger(TransactionService.class);
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

    @KafkaListener(topics = CommonConstants.WALLET_UPDATED_TOPIC, groupId="EWallet_Group")      //will act as a consumer
    public void updateTransaction(String msg) throws ParseException, JsonProcessingException {

        JSONObject data = (JSONObject) new JSONParser().parse(msg);

        String sender = (String) data.get("sender");
        String receiver = (String) data.get("receiver");
        Double amount = (Double) data.get("amount");
        String transactionId = (String) data.get("transactionId");
        WalletUpdateStatus walletUpdateStatus = (WalletUpdateStatus) data.get("walletUpdateStatus");

        if (walletUpdateStatus == WalletUpdateStatus.SUCCESS) {
             transactionRepository.updateTransaction(transactionId, TransactionStatus.SUCCESS);

        } else {
            transactionRepository.updateTransaction(transactionId, TransactionStatus.FAILED);
        }

//        //String senderMsg = "Hi, your transaction with Id " + transactionId + " is " + walletUpdateStatus;
//
//        JSONObject senderEmailObj = new JSONObject();
//        senderEmailObj.put("sender", sender);
//        senderEmailObj.put("sender", sender);

        //publish the event after validating and updating wallets of sender and receiver which will be consumed by the consumers
        JSONObject json = new JSONObject();
        json.put("sender", sender);
        json.put("receiver", receiver);
        json.put("amount", amount);
        json.put("transactionId", transactionId);
        json.put("WalletUpdateStatus", walletUpdateStatus);

        kafkaTemplate.send(CommonConstants.TRANSACTION_COMPLETION_TOPIC, objectMapper.writeValueAsString(json));

    }
}
