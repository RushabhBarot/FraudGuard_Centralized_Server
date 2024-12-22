package org.example.centralserver.dto;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString
public class GraphDataDTO {
    private List<AccountNodeDTO> nodes;
    private List<TransactionEdgeDTO> edges;

    // Getters and Setters
    public List<AccountNodeDTO> getNodes() {
        return nodes;
    }

    public void setNodes(List<AccountNodeDTO> nodes) {
        this.nodes = nodes;
    }

    public List<TransactionEdgeDTO> getEdges() {
        return edges;
    }

    public void setEdges(List<TransactionEdgeDTO> edges) {
        this.edges = edges;
    }
}
