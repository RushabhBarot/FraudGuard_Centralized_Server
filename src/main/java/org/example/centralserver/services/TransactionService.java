package org.example.centralserver.services;


import org.example.centralserver.Entity.Account;
import org.example.centralserver.Entity.AccountNeo4J;
import org.example.centralserver.Entity.Transection;
import org.example.centralserver.helper.AccountLoader;
import org.example.centralserver.helper.GetAccounts;
import org.example.centralserver.mapper.Bank1TransactionMapper;
import org.example.centralserver.repo.mongo.AccountRepo;
import org.example.centralserver.repo.mongo.TransectionRepo;
import org.example.centralserver.repo.neo4j.AccountNeo4jRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TransactionService {


    private static  ModelMapper modelMapper = new ModelMapper();

//    @Autowired
//    private ModelMapper modelMapper;

    @Autowired
    private RestTemplate restTemplate;


    @Autowired
    private AccountLoader accountLoader;

    @Autowired
    private AccountNeo4jRepository accountNeo4jRepository;



    @Autowired
    private TransectionRepo transectionRepo;

    @Autowired
    private AccountRepo accountRepo;

    @Autowired
    private Bank1TransactionMapper bank1TransactionMapper;

    @Autowired
    private GetAccounts getAccounts;

    @Autowired
    RedisService redisService;

    private final String bankApiUrl = "http://localhost:8080/transactions"; // Replace with actual API URL

    // Scheduled task to fetch and process transactions every 2 minutes
    @Scheduled(fixedRate = 120000) //2 min
    public void processTransactions() {
        System.out.println("Fetching transactions from bank API..."+LocalDateTime.now());

        // Fetch transactions from the Bank2's API
        List<?> response = restTemplate.getForObject(bankApiUrl, List.class);
        System.out.println(response);

        //we pass this list to mapper
        List<Transection> transactions = bank1TransactionMapper.mapTransactions(response);

        for (Transection transaction : transactions) {
            //save each transaction in our database of central sys
            transectionRepo.save(transaction);
            processTransaction(transaction);
        }
    }

    // Process each transaction
    private void processTransaction(Transection transaction) {
        String sender = transaction.getSender();
        String receiver = transaction.getReceiver();

        // Process sender account
        Account senderAccount = accountLoader.loadAccountIntoRedis(sender, transaction);

        // Process receiver account
        Account receiverAccount = accountLoader.loadAccountIntoRedis(receiver, transaction);

        // Check for suspicious activity
        boolean isSenderSuspicious = checkSuspiciousAccount(senderAccount, transaction);
        boolean isReceiverSuspicious = checkSuspiciousAccount(receiverAccount, transaction);

        System.out.println("Source Account: " + senderAccount);

        AccountNeo4J senderAccountNeo4j = modelMapper.map(senderAccount, AccountNeo4J.class);
        AccountNeo4J receiverAccountNeo4j = modelMapper.map(receiverAccount, AccountNeo4J.class);



        //Save suspicious accounts and transactions to Redis, and others to MongoDB
        if(transaction.getAmt() > 2000){
            redisService.saveObject(transaction.getId(), transaction);

            System.out.println("Saving AccountNeo4J: " + senderAccountNeo4j);

            // Check if the account already exists in the database before saving
            if (accountNeo4jRepository.existsById(senderAccountNeo4j.getAccId())) {
                System.out.println("Account exists: " + senderAccountNeo4j.getAccId());
            } else {
                accountNeo4jRepository.save(senderAccountNeo4j);
                System.out.println("Saving AccountNeo4J: " + senderAccountNeo4j);
            }

            if (accountNeo4jRepository.existsById(receiverAccountNeo4j.getAccId())) {
                System.out.println("Account exists: " + receiverAccountNeo4j.getAccId());
            } else {
                accountNeo4jRepository.save(receiverAccountNeo4j);
                System.out.println("Saving AccountNeo4J: " + receiverAccountNeo4j);
            }



            createTransactionRelationship(senderAccountNeo4j, receiverAccountNeo4j, transaction);


        } else if (isSenderSuspicious || isReceiverSuspicious) {
            // Check if the account already exists in the database before saving
            if (accountNeo4jRepository.existsById(senderAccountNeo4j.getAccId())) {
                System.out.println("Account exists: " + senderAccountNeo4j.getAccId());
            } else {
                accountNeo4jRepository.save(senderAccountNeo4j);
                System.out.println("Saving AccountNeo4J: " + senderAccountNeo4j);
            }

            if (accountNeo4jRepository.existsById(receiverAccountNeo4j.getAccId())) {
                System.out.println("Account exists: " + receiverAccountNeo4j.getAccId());
            } else {
                accountNeo4jRepository.save(receiverAccountNeo4j);
                System.out.println("Saving AccountNeo4J: " + receiverAccountNeo4j);
            }


            createTransactionRelationship(senderAccountNeo4j, receiverAccountNeo4j, transaction);

        } else {
            //not suspicious means delete it
            redisService.deleteKey(sender);
            redisService.deleteKey(receiver);
        }

        accountRepo.save(senderAccount);
        accountRepo.save(receiverAccount);
    }

        // Fetch account from Redis or database, update, and return

    // Check if the account is suspicious based on the criteria
    private boolean checkSuspiciousAccount(Account account, Transection transaction) {
        boolean isSuspicious = false;

        // Check if frequency exceeds 50
        if (account.getFreq() > 10) {
            isSuspicious = true;
        }

        // Check if the gap between last transaction and today is more than a year
        if (account.getLastTransaction() != null) {
            long yearsGap = ChronoUnit.YEARS.between(account.getLastTransaction(), LocalDateTime.now());
            if (yearsGap > 1) {
                isSuspicious = true;
            }
        }

        return isSuspicious;
    }

    private void createTransactionRelationship(AccountNeo4J sender, AccountNeo4J receiver, Transection transaction) {
        // Define a relationship, e.g., "SENT"
        sender.addTransactionTo(receiver, transaction);


        if (accountNeo4jRepository.existsById(sender.getAccId())) {
            System.out.println("Account exists: " + sender.getAccId());
        } else {
            accountNeo4jRepository.save(sender);
            System.out.println("Saving AccountNeo4J: " + sender);
        }
    }


}
