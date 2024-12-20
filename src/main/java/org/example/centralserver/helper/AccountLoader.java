package org.example.centralserver.helper;


import org.example.centralserver.Entity.Account;
import org.example.centralserver.Entity.Transection;
import org.example.centralserver.services.AccountService;
import org.example.centralserver.services.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountLoader {

    @Autowired
    private RedisService redisService;

    @Autowired
    private AccountService accountService;

    @Autowired
    GetAccounts getAccounts;

    public Account loadAccountIntoRedis(String accId , Transection transection){

        try {


            Account account = redisService.getObject(accId, Account.class);

            //if account is not in redis then find it in our central sys database
            if (account == null) {
                Account account1 = accountService.getAccountByAccId(accId);
                //if not found even in database means we have to fetch it from bank2 and map it to
                //our account and then return it
                if (account1 == null) {
                    account1 = getAccounts.getAccount(accId);
                }

                account1.setLastTransaction(transection.getCreatedDate());
                account1.setFreq(account1.getFreq() + 1);

                redisService.saveObject(accId, account1);
                return account1;
            }

            //if already in redis
            account.setLastTransaction(transection.getCreatedDate());
            account.setFreq(account.getFreq() + 1);
            redisService.saveObject(accId, account);
            return account;

        }
        catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }


}
