package org.example.centralserver.repo.neo4j;


import org.example.centralserver.Entity.Account;
import org.example.centralserver.Entity.AccountNeo4J;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.Optional;


public interface AccountNeo4jRepository extends Neo4jRepository<AccountNeo4J, String> {
    Optional<AccountNeo4J> findByAccId(String accId);
}
