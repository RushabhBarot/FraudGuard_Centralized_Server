package org.example.centralserver.helper;


import org.example.centralserver.Entity.Account;
import org.example.centralserver.Entity.Transection;
import org.example.centralserver.services.AccountService;
import org.example.centralserver.services.RedisService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Service
public class AccountLoader {

    @Autowired
    private RedisService redisService;

    @Autowired
    private RedissonClient redisson;

    @Autowired
    private AccountService accountService;

    @Autowired
    GetAccounts getAccounts;

    public Account loadAccountIntoRedis(String accId , Transection transection, String bank){
        String redisKey = bank + "_" + accId;
        RLock lock = redisson.getLock(redisKey + "_lock");

        try {
            if (lock.tryLock(10, 5, TimeUnit.SECONDS)) { // Acquire lock
                try {
                    Account account = redisService.getObject(redisKey, Account.class);

                    //if account not there in redis then find it in db of central system
                    if (account == null) {
                        account = accountService.getaccount(redisKey);

                        //if account is not stored in db of central system then get it from respective bank
                        if (account == null) {
                            account = getAccounts.getAccount(accId, bank);

                            //if no such account exists then throw error
                            if (account == null) {
                                throw new RuntimeException("Account not found for accId: " + accId);
                            }
                        }
                    }

                    account.setLastTransaction(transection.getCreatedDate());
                    account.setFreq(account.getFreq() + 1);

                    boolean isAccountSuspicious = checkSuspiciousAccount(account, transection);

                    if (transection.getAmt() > 5000 || isAccountSuspicious) {
                        account.setSuspicious(true);
                    }

                    redisService.saveObject(redisKey, account);
                    //store account with this key in redis
                    redisService.addToSet("accounts", redisKey);

                    if (transection.getAmt() > 5000) {
                        redisService.saveObject(transection.getId(), transection);
                        redisService.addToSet("transaction", transection.getId());
                    }

                    return account;

                } finally {
                    lock.unlock(); // Release the lock
                }
            } else {
                throw new RuntimeException("Could not acquire lock for accId: " + accId);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread interrupted while acquiring lock for accId: " + accId, e);
        }
    }

    private boolean checkSuspiciousAccount(Account account, Transection transaction) {
        boolean isSuspicious = account.getFreq() > 10;

        if (account.getLastTransaction() != null) {
            long yearsGap = ChronoUnit.YEARS.between(account.getLastTransaction(), LocalDateTime.now());
            if (yearsGap > 1) {
                isSuspicious = true;
            }
        }

        return isSuspicious;
    }
}
