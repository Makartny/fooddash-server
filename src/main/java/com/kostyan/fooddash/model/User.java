package com.kostyan.fooddash.model;

import jakarta.persistence.*;


@Entity
@Table(name = "users")  // Создаём в Докере отдельную физическую таблицу 'users'
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // unique = true гарантирует: в системе не может появиться двух людей с одинаковым логином!
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 100) // Длина 100, потому что пароль будет храниться в зашифрованном виде (хэш)
    private String password;

    @Column(nullable = false, length = 20)
    private String role; // Роль пользователя: CLIENT (клиент) или ADMIN (администратор)

    // Constructors

    // Пустой конструктор для Hibernate
    public User() {

    }

    // Конструктор для быстрого создания пользователя в коде
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Блок Геттеров и Сеттеров (наши стандартные провода)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }






} // end class User
