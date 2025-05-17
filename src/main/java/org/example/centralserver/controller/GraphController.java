package org.example.centralserver.controller;

import org.example.centralserver.Entity.neo4j.AccountNode;
import org.example.centralserver.dto.GraphResponseDTO;
import org.example.centralserver.services.GraphService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/graph")
public class GraphController {

    private final GraphService graphService;

    public GraphController(GraphService graphService) {
        this.graphService = graphService;
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<AccountNode>> getAllAccounts() {
        return ResponseEntity.ok(graphService.getAllAccounts());
    }

    @GetMapping("/{accountNumber}/{level}")
    public ResponseEntity<GraphResponseDTO> getNetworkGraph(
            @PathVariable String accountNumber,
            @PathVariable Integer level) {
        return ResponseEntity.ok(graphService.getNetworkGraph(accountNumber, level));
    }
}
