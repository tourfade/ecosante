package com.kamitsoft.ecosante.services;

public class AuthenticationInfo {
   // private String account;
    private String username;
    private String password;

    public AuthenticationInfo(String account, String email, String password) {
        //this.account = account;
        this.username = email;
        this.password = password;
    }
}

