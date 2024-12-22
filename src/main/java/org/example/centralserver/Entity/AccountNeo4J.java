package org.example.centralserver.Entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
@Getter
@Setter
@Node("Account")
public class AccountNeo4J  {
    @Serial
    private static final long serialVersionUID = 1L; // Add a serialVersionUID for better version control during serialization

    @Id
    private String id;
    private String accId;
    private String bank;
    private String user;
    private double freq=0;//avg transactions per day...


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) // Ensures consistent formatting
    private LocalDateTime lastTransaction=null;

    private int regularIntervelTransection=0;
    //let's say every day at 2 pm so this will increase
    private boolean isSuspicious=false;


    @Relationship(type = "SENT", direction = Relationship.Direction.OUTGOING)
    private List<TransectionNeo4J> transection = new ArrayList<>();

    private List<String> nominees=new ArrayList<String>();

    public AccountNeo4J(){}

    public AccountNeo4J(String accId, String bank, String user, List<String> nominees) {
        this.accId = accId;
        this.bank = bank;
        this.user = user;
        this.nominees = nominees;
    }
    public String getAccId() {
        return accId;
    }

    public void setAccId(String accId) {
        if (accId != null && !accId.trim().isEmpty()) {
            this.accId = accId.trim();
        } else {
            throw new IllegalArgumentException("Account ID cannot be null or empty");
        }
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        if (bank != null && !bank.trim().isEmpty()) {
            this.bank = bank.trim();
        } else {
            throw new IllegalArgumentException("Bank cannot be null or empty");
        }
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        if (user != null && !user.trim().isEmpty()) {
            this.user = user.trim();
        } else {
            throw new IllegalArgumentException("User cannot be null or empty");
        }
    }

    public double getFreq() {
        return freq;
    }

    public void setFreq(double freq) {
        if (freq >= 0) {
            this.freq = freq;
        } else {
            throw new IllegalArgumentException("Frequency cannot be negative");
        }
    }

    public LocalDateTime getLastTransaction() {
        return lastTransaction;
    }

    public void setLastTransaction(LocalDateTime lastTransaction) {
        if (lastTransaction != null) {
            this.lastTransaction = lastTransaction;
        } else {
            throw new IllegalArgumentException("Last Transaction cannot be null");
        }
    }

    public boolean isSuspicious() {
        return isSuspicious;
    }

    public void setSuspicious(boolean suspicious) {
        isSuspicious = suspicious;
    }

    public List<TransectionNeo4J> getTransactions() {
        return transection != null ? new ArrayList<>(transection) : new ArrayList<>();
    }

    public void setTransactions(List<TransectionNeo4J> transactions) {
        if (transactions != null) {
            this.transection = new ArrayList<>(transactions);
        } else {
            this.transection = new ArrayList<>();
        }
    }

    public List<String> getNominees() {
        return nominees != null ? new ArrayList<>(nominees) : new ArrayList<>();
    }

    public void setNominees(List<String> nominees) {
        if (nominees != null) {
            this.nominees = new ArrayList<>(nominees);
        } else {
            this.nominees = new ArrayList<>();
        }
    }

    public void addTransactionTo(AccountNeo4J receiver, Transection transaction) {
        // Create the relationship between sender and receiver
        System.out.println("adding edge");
        TransectionNeo4J transectionNeo4J=new TransectionNeo4J(transaction,receiver);
        System.out.println("TransactionNeo4J entity: "+transectionNeo4J);
        this.transection.add(transectionNeo4J);
    }

    @Override
    public String toString() {
        return "AccountNeo4J{" +
                "id='" + id + '\'' +
                ", accId='" + accId + '\'' +
                ", bank='" + bank + '\'' +
                ", user='" + user + '\'' +
                ", freq=" + freq +
                ", lastTransaction=" + lastTransaction +
                ", regularIntervelTransection=" + regularIntervelTransection +
                ", isSuspicious=" + isSuspicious +
                ", nominees=" + nominees +
                '}';
    }

}
