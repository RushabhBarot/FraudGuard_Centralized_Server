package org.example.centralserver.services;

import org.example.centralserver.dto.AccountNodeDTO;
import org.example.centralserver.dto.GraphDataDTO;
import org.example.centralserver.dto.TransactionEdgeDTO;
import org.example.centralserver.repo.neo4j.AccountNeo4jRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GraphDataService {

    private final AccountNeo4jRepository accountNeo4jRepository;

    public GraphDataService(AccountNeo4jRepository accountNeo4jRepository) {
        this.accountNeo4jRepository = accountNeo4jRepository;
    }

    public GraphDataDTO fetchGraphData() {
        List<AccountNeo4J> accounts = accountNeo4jRepository.findAll();
        GraphDataDTO graphDataDTO = new GraphDataDTO();

        List<AccountNodeDTO> nodes = new ArrayList<>();
        List<TransactionEdgeDTO> edges = new ArrayList<>();

        for (AccountNeo4J account : accounts) {
            // Add nodes
            AccountNodeDTO node = new AccountNodeDTO();
            node.setAccId(account.getAccId());
            node.setUser(account.getUser());
            node.setBank(account.getBank());
            node.setSuspicious(account.isSuspicious());
            nodes.add(node);

            // Add edges
            for (TransectionNeo4J transaction : account.getTransactions()) {
                TransactionEdgeDTO edge = new TransactionEdgeDTO();
                edge.setSource(account.getAccId());
                edge.setTarget(transaction.getReceiver().getAccId());
                edge.setAmount(transaction.getAmount());
                edges.add(edge);
            }
        }

        graphDataDTO.setNodes(nodes);
        graphDataDTO.setEdges(edges);
        System.out.println("Graph Data: "+graphDataDTO );
        return graphDataDTO;
    }
}
