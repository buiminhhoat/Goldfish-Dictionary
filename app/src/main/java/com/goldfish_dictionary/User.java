package com.goldfish_dictionary;

public class User {
    int user_id;
    String username;
    String firstName;
    String lastName;
    String email;
    String passwordHash;

    public User() {
    }

    public User(int user_id, String username, String firstName, String lastName, String email, String passwordHash) {
        this.user_id = user_id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.passwordHash = passwordHash;
    }
}
