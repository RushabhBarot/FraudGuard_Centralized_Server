package org.example.centralserver.services;

import org.example.centralserver.Entity.Account;
import org.example.centralserver.Entity.Transection;
import org.example.centralserver.helper.AccountLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class TransactionProcessorService {
    @Autowired
    AccountLoader accountLoader;

    //we have configured taskExecutor before this will process each transaction on different thread
    @Async("taskExecutor")
    public CompletableFuture<Void> processTransactionAsync(Transection transaction, String bank) {
        try {
            String sender = transaction.getSender();
            String receiver = transaction.getReceiver();

            System.out.println("Processing transaction " + transaction +
                    " on thread: " + Thread.currentThread().getName());


            //process transactions and based on that do work
            Account senderAccount = accountLoader.loadAccountIntoRedis(sender, transaction, bank);
            Account receiverAccount = accountLoader.loadAccountIntoRedis(receiver, transaction, bank);


            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            //if failure occurs
            return CompletableFuture.failedFuture(e);
        }
    }
}
