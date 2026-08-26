package com.kostyan.fooddash.controller;

import com.kostyan.fooddash.model.User;
import com.kostyan.fooddash.repository.UserRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserRepository userRepository;

    // Спринг сам подставит сюда наш новый пульт управления пользователями
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    // РУЧКА РЕГИСТРАЦИИ: Сюда будут стучаться новые клиенты!
    @PostMapping("/register")
    public String registerUser(@RequestBody User newUser) {

        // 1. Проверяем нашим кастомным методом, нет ли уже человека с таким логином
        if (userRepository.findByUsername(newUser.getUsername()).isPresent()) {
            return "❌ Ошибка: Пользователь с логином '" + newUser.getUsername() + "' уже зарегистрирован в системе!";
        }
        // 2. Назначаем новичку стандартную роль CLIENT (Клиент)
        newUser.setRole("CLIENT");

        // 3. Сохраняем пользователя в Докер!
        userRepository.save(newUser);
        return "✅ Успех! Пользователь '" + newUser.getUsername() + "' успешно добавлен в систему FoodDash!";
    }
}
