package com.playtech.proov.server;


import java.io.Serializable;

/**
 * Keeps information about user identity.
 */
public class AuthenticationContext implements Serializable {

    private static final long serialVersionUID = 1;

    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
