package com.example.android.hospitalmanagementsystem;

/**
 * Created by DELL on 11/2/2017.
 */

public class User {
    String name;
    String email;
    String permission;

    public User(String email, String name, String permission) {
        this.name = name;
        this.email = email;
        this.permission = permission;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPermission() {
        return permission;
    }
}
