package org.example.centralserver.repo.neo4j;


import org.example.centralserver.Entity.Account;
import org.example.centralserver.Entity.AccountNeo4J;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;
import java.util.Optional;


public interface AccountNeo4jRepository extends Neo4jRepository<AccountNeo4J, String> {
    Optional<AccountNeo4J> findByAccId(String accId);

    @Query("MATCH (a:Account)-[r:SENT]->(b:Account) RETURN a, r, b")
    List<AccountNeo4J> executeGraphQuery(String query);


}
