package org.example.centralserver.dto;

import lombok.Data;

@Data
public class AccountNodeDTO {
    private String accId;
    private String user;
    private String bank;
    private boolean isSuspicious;

    // Getters and Setters
    public String getAccId() {
        return accId;
    }

    public void setAccId(String accId) {
        this.accId = accId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public boolean isSuspicious() {
        return isSuspicious;
    }

    public void setSuspicious(boolean suspicious) {
        isSuspicious = suspicious;
    }
}
