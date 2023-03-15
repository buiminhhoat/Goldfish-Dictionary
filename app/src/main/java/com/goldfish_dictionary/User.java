package com.goldfish_dictionary;

public class User {
    int user_id;
    String username;
    String first_name;
    String last_name;
    String email;
    String password_hash;
    byte[] avatar_bitmap;

    public User() {
    }

    public User(int user_id, String username, String first_name, String last_name, String email, String password_hash, byte[] avatar_bitmap) {
        this.user_id = user_id;
        this.username = username;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.password_hash = password_hash;
        this.avatar_bitmap = avatar_bitmap;
    }
}
