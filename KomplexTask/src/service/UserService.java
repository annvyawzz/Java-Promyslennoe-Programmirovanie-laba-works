package com.example.springbootxml.service;

import com.example.springbootxml.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private List<User> users = new ArrayList<>();

    public UserService() {
        // Тестовые пользователи
        User librarian = new User();
        librarian.setId(1);
        librarian.setUsername("librarian");
        librarian.setPassword("password");
        librarian.setFullName("Библиотекарь Админ");

        User reader = new User();
        reader.setId(2);
        reader.setUsername("reader");
        reader.setPassword("password");
        reader.setFullName("Иван Читатель");

        users.add(librarian);
        users.add(reader);
    }

    public User getUserByUsername(String username) {
        return users.stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
    }

    public List<User> getAllReaders() {
        return users; // Временно возвращаем всех
    }

    public void registerUser(User user) {
        int maxId = users.stream()
                .mapToInt(User::getId)
                .max()
                .orElse(0);
        user.setId(maxId + 1);
        users.add(user);
    }
}