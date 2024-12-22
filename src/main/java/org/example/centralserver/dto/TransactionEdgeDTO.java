package org.example.centralserver.dto;

import lombok.Data;

@Data
public class TransactionEdgeDTO {
    private String source; // Sender's accId
    private String target; // Receiver's accId
    private double amount;

    // Getters and Setters
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
