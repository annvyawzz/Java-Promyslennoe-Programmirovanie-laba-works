package com.example.springbootxml.model;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class User {
    private Integer id;
    private String username;
    private String password;
    private String email;
    private String fullName;

    // Временно упрощаем
    public boolean isLibrarian() {
        return username.equals("librarian");
    }

    public boolean isReader() {
        return username.equals("reader");
    }
}