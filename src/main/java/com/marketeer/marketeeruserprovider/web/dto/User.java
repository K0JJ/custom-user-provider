package com.marketeer.marketeeruserprovider.web.dto;

import java.util.List;

public record User(
        // future for testing purposes it iwll be easier to use the username
        // UUID userId,

        String username,
        String firstName,
        String lastName,

        String password,

        String email,

        String dateOfBirth,

        List<String> roles

) {

    public User(String username, String password) {
        this(username, null, null, password, null, null, null);
    }

    public User(String username, String firstName, String lastName, String password, String email, String dateOfBirth, List<String> roles) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.roles = roles;
    }
}
