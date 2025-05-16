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


            System.out.println("Processing transaction " + transaction +
                    " on thread: " + Thread.currentThread().getName());


            //process transactions and based on that do work
            Account senderAccount = (Account) transaction.getSender();
            senderAccount=accountLoader.loadAccountIntoRedis(senderAccount,transaction,bank,true);
            Account receiverAccount = (Account) transaction.getReceiver();
            receiverAccount = accountLoader.loadAccountIntoRedis(receiverAccount, transaction, bank,false);


            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            //if failure occurs
            return CompletableFuture.failedFuture(e);
        }
    }
}
