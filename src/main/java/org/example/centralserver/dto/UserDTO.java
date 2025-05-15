package org.example.centralserver.dto;

import org.example.centralserver.Entity.Transection;
import org.example.centralserver.Entity.User;

import java.util.List;

public class UserDTO {

    private User user;
    private List<Transection>transections;

    public UserDTO() {

    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Transection> getTransections() {
        return transections;
    }

    public void setTransections(List<Transection> transections) {
        this.transections = transections;
    }
}
