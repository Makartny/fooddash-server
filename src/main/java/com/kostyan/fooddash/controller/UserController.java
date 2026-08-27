package com.kostyan.fooddash.controller;

import com.kostyan.fooddash.model.User;
import com.kostyan.fooddash.repository.UserRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;

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
    } // end registerUser

    // ==========================================
    // ВХОДЯЩИЕ ВОРОТА: АВТОРИЗАЦИЯ И ВХОД (ЛОГИН)
    // ==========================================

    @PostMapping("/login") // Слушаем POST-запросы на адрес /login
    public String loginUser(@RequestBody User loginData) {
        // @RequestBody поймает JSON с логином/паролем из Postman и превратит в объект loginData

        // ШАГ 1: Пытаемся найти пользователя в базе данных Докера по его логину
        Optional<User> userFromDb = userRepository.findByUsername(loginData.getUsername());

        // ШАГ 2: Проверяем, существует ли вообще человек с таким логином
        if (userFromDb.isEmpty()) {
            return "❌ Ошибка: Пользователь с логином '" + loginData.getUsername() + "' не найден в системе!";
        }

        // ШАГ 3: Если человек найден, достаём его из обёртки Optional
        User realUser = userFromDb.get();

        // ШАГ 4: Проверяем, совпадает ли присланный пароль с тем, что лежит в базе данных
        if (!realUser.getPassword().equals(loginData.getPassword())) {
            return "❌ Ошибка: Неверный пароль! Доступ заблокирован!";
        }

        // ШАГ 5: Если все проверки пройдены — выдаём триумфальный пропуск!
        return "🔑 Доступ разрешён! Добро пожаловать в FoodDash, " + realUser.getUsername() + " [" + realUser.getRole() + "]!";

    } // end method loginUser


} // end class UserController
