package com.marketeer.marketeeruserprovider.client.mock;

import com.marketeer.marketeeruserprovider.client.AuthorizationClient;

public class AuthorizationClientImpl implements AuthorizationClient {


    // for now, we will only check if the password starts with {bcrypt-12}
    @Override
    public boolean checkPassword(String userId, String password) {
        if(password == null  || password.isBlank() || password.isEmpty()) return false;
        return password.startsWith("{bcrypt-12}");
    }
}
