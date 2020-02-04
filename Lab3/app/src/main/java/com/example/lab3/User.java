package com.example.lab3;

public class User {
    String username;
    String password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    public boolean isUser(String username, String password) {
        if (this.username.equals(username) && this.password.equals(password)) {
            return true;
        }
        else return false;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return username + "," + password;
    }
}
