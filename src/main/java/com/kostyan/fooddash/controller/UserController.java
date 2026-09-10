package com.kostyan.fooddash.controller;

import com.kostyan.fooddash.model.User;
import com.kostyan.fooddash.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

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

    // =======================================================
    // БОЕВАЯ ЗАДАЧА №2: БЕЗОПАСНАЯ СМЕНА ПАРОЛЯ (PATCH + JSON)
    // =======================================================
    @PatchMapping("/users/change-password") // Ловим PATCH-запрос на частичную заплатку данных!
    public String changePassword(@RequestBody User inputData) {
        // @RequestBody заставит Спринг вскрыть грузовой отсек JSON из Postman и собрать объект inputData

        // 1. Из прилетевшего JSON-объекта достаем логин, чтобы найти человека в Докере
        String username = inputData.getUsername();

        // 2. Ищем пользователя в базе через наш пульт userRepository
        Optional<User> userFromDb = userRepository.findByUsername(username);

        // 3. Капкан безопасности: если такого юзера нет в Докере — рубим операцию!
        if (userFromDb.isEmpty()) {
            return "❌ Ошибка: Пользователь с логином '" + username + "' не найден в системе!";
        }

        User realUser = userFromDb.get();
        // 4. Пропускаем НОВЫЙ сырой пароль из Postman через наш крипто-шифратор BCrypt
        // (Кстати, пульт passwordEncoder у нас уже внедрён в этот класс с прошлых занятий!)
        String newHashedPassword = passwordEncoder.encode(inputData.getPassword());


        // 5. Перезаписываем только поле пароля в оперативной памяти Java
        realUser.setPassword(newHashedPassword);

        // 6. Сохраняем обновленного юзера обратно в Докер.
        // Hibernate видит существующий ID и просто обновляет ячейку пароля на жёстком диске!
        userRepository.save(realUser);

        return "🔒 Безопасность обновлена! Пароль для пользователя '" + username +
                "' успешно захеширован по стандарту BCrypt и перезаписан в Docker!";
    } // end changePassword

    // =======================================================
    // БОЕВАЯ ЗАДАЧА №6: VIP-СТАТУС (АВТО-СМЕНА РОЛИ В ДОКЕРЕ)
    // =======================================================

    @PostMapping("/users/upgrade-vip") // Ловим POST-запрос
    public String upgradeUserToVip(@RequestParam String username) {
        // @RequestParam откусит логин прямо из адресного хвостика ссылки

        // ШАГ 1: Ищем этого человека в Докере через наш пульт userRepository
        Optional<User> userFromDb = userRepository.findByUsername(username);

        // ШАГ 2: Защитный капкан! Если такого логина вообще нет в базе — рубим операцию!
        if (userFromDb.isEmpty()) {
            return "❌ Ошибка: Некого повышать! Пользователь '" + username + "' не зарегистрирован в FoodDash!";
        } // end if

        User realUser = userFromDb.get();

        // ШАГ 3: Защита от дурака. Если у него УЖЕ стоит роль VIP — незачем мучить жесткий диск!
        if ("VIP".equals(realUser.getRole())) {
            return "💎 Хьюстон, у нас отмена! Клиент '" + username + "' уже является VIP-пользователем!";
        } // end if

        // ШАГ 4: Переписываем ячейку роли в оперативной памяти Java Core
        realUser.setRole("VIP");

        // ШАГ 5: Нажимаем кнопку .save() на пульте репозитория.
        // Hibernate видит знакомый ID пользователя и просто обновляет ячейку

        userRepository.save(realUser);

        return "👑 Триумф! Пользователю '" + username + "' официально присвоен элитный статус VIP!" +
                " Скидка на все бургеры активирована!";

    } // end upgradeUserToVip


} // end class UserController
