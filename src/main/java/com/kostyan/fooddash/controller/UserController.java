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
    // НАШ НОВЫЙ КРИПТОГРАФИЧЕСКИЙ ПУЛЬТ ДЛЯ ШИФРОВАНИЯ ПАРОЛЕЙ!
    private final org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder passwordEncoder;

    // Спринг сам зайдёт в сейф памяти, достанет UserRepository и наш BCryptPasswordEncoder, и вложит в этот конструктор!
    public UserController(UserRepository userRepository,
                          org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ==========================================
    // ВХОДЯЩИЕ ВОРОТА: РЕГИСТРАЦИЯ С ШИФРОВАНИЕМ
    // ==========================================
    @PostMapping("/register")
    public String registerUser(@RequestBody User newUser) {

        // 1. Проверяем, свободен ли логин
        if (userRepository.findByUsername(newUser.getUsername()).isPresent()) {
            return "❌ Ошибка: Пользователь с логином '" + newUser.getUsername() + "' уже зарегистрирован!";
        }

        // ШАГ 2: БРОНЕБОЙНОЕ ХЭШИРОВАНИЕ ПАРОЛЯ ПЕРЕД КЛАДОВКОЙ!
        // Берем сырой текст, пропускаем через BCrypt и перезаписываем его обратно в объект юзера!
        String hashedPassword = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(hashedPassword);

        // 3. Ставим роль по умолчанию и сохраняем зашифрованного юзера в Докер!
        newUser.setRole("CLIENT");
        userRepository.save(newUser);

        return "✅ Успех! Пользователь '" + newUser.getUsername() + "' успешно добавлен в систему FoodDash!";
    }

    // ==========================================
    // ВХОДЯЩИЕ ВОРОТА: АВТОРИЗАЦИЯ И ВХОД (ЛОГИН)
    // ==========================================
    @PostMapping("/login")
    public String loginUser(@RequestBody User loginData) {

        // 1. Ищем человека по логину в Докере
        Optional<User> userFromDb = userRepository.findByUsername(loginData.getUsername());

        if (userFromDb.isEmpty()) {
            return "❌ Ошибка: Пользователь с логином '" + loginData.getUsername() + "' не найден в системе!";
        }

        User realUser = userFromDb.get();

        // ШАГ 4: СИНЬОРСКАЯ СВЕРКА ЧЕРЕЗ КРИПТО-МАТЧИНГ!
        // Передаем два параметра: (СыройПарольИзИнтернета, ЗашифрованныйПарольИзБазы)
        // Метод .matches() сам поймет, подходит ли ключ к замку!
        if (!passwordEncoder.matches(loginData.getPassword(), realUser.getPassword())) {
            return "❌ Ошибка: Неверный пароль! Доступ заблокирован!";
        }

        return "🔑 Доступ разрешён! Добро пожаловать в FoodDash, " + realUser.getUsername() +
                " [" + realUser.getRole() + "]!";
    }
}
