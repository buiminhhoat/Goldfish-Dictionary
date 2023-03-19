package com.goldfish_dictionary;

public class User {
    private int user_id;
    private String username;
    private String first_name;
    private String last_name;
    private String email;
    private String password_hash;
    private byte[] avatar_bitmap;

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

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    public byte[] getAvatar_bitmap() {
        return avatar_bitmap;
    }

    public void setAvatar_bitmap(byte[] avatar_bitmap) {
        this.avatar_bitmap = avatar_bitmap;
    }
}
