package org.example.centralserver.repo.mongo;

import org.example.centralserver.Entity.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AccountRepo extends MongoRepository<Account, String> {

    Optional<Account> findByAccId(String accId);

}

