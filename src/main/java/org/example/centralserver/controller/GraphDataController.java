package org.example.centralserver.controller;

import org.example.centralserver.dto.GraphDataDTO;
import org.example.centralserver.services.GraphDataService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/graph")
public class GraphDataController {

    private final GraphDataService graphDataService;

    public GraphDataController(GraphDataService graphDataService) {
        this.graphDataService = graphDataService;
    }

    @GetMapping
    public GraphDataDTO getGraphData() {
        return graphDataService.fetchGraphData();
    }
}
