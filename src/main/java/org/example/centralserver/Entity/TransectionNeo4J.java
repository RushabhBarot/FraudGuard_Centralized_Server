package org.example.centralserver.Entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.LocalDateTime;

@Setter
@Getter
@RelationshipProperties
public class TransectionNeo4J {

    // Getters and Setters
    @Id
    @GeneratedValue
    private Long id;  // Add an ID for the relationship property entity

    private double amount;
    private LocalDateTime timestamp;

    @TargetNode
    private AccountNeo4J receiver;  // Specify the target node (Receiver in this case)

    public TransectionNeo4J(Transection transection, AccountNeo4J receiver) {
        System.out.println("Creating TransectionNeo4J edge");
        this.amount = transection.getAmt();
        this.timestamp = transection.getCreatedDate();
        this.receiver = receiver;  // Set the receiver as part of the relationship
    }

    public AccountNeo4J getReceiver() {
        return receiver;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
