package com.kostyan.fooddash.controller;

import com.kostyan.fooddash.model.Order;
import com.kostyan.fooddash.model.User;
import com.kostyan.fooddash.repository.OrderRepository;
import com.kostyan.fooddash.repository.UserRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController // Приказ Спрингу: «Этот класс — внешний пульт, он слушает интернет на порту 8080!»
public class OrderController {
    // Объявляем два внутренних скрытых пульта для работы с Докером
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    // Конструктор: Спринг сам заглянет в свой сейф, достанет эти пульты и вложит их в круглые скобки!
    public OrderController(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    // ==========================================
    // ВХОДЯЩИЕ ВОРОТА: ОФОРМЛЕНИЕ НОВОГО ЗАКАЗА
    // ==========================================
    @PostMapping("/orders") // Ловим POST-метод на адрес /orders
    public String createOrder(@RequestBody Order incomingOrder) {
        // @RequestBody перехватит JSON из сети и превратит в объект incomingOrder

        // ШАГ 1: Из прилетевшего заказа достаем объект пользователя и его логин
        String customerUsername = incomingOrder.getUser().getUsername();

        // ШАГ 2: Ищем этого пользователя в Докере через наш пульт userRepository
        Optional<User> userFromDb = userRepository.findByUsername(customerUsername);

        // ШАГ 3: Защитный капкан! Если человека с таким логином нет в базе — рубим операцию!
        if (userFromDb.isEmpty()) {
            return "❌ Ошибка: Заказ отклонен! Пользователь '" + customerUsername + "' не зарегистрирован в системе!";
        }

        // ШАГ 4: Если пользователь найден, достаем его живой объект из обертки Optional
        User realUser = userFromDb.get();

        // ШАГ 5: Привязываем этот реальный объект пользователя к нашему новому заказу
        incomingOrder.setUser(realUser);

        // ШАГ 6: Принудительно выставляем заказу стартовый статус "PENDING" (Ожидает обработки)
        incomingOrder.setStatus("PENDING");

        // ШАГ 7: Нажимаем встроенную кнопку .save() на пульте orderRepository и уносим чек в Докер!
        orderRepository.save(incomingOrder);

        // ШАГ 8: Возвращаем триумфальный ответ в Postman!
        return "📦 Успех! Заказ для пользователя '" + realUser.getUsername() + "' успешно оформлен! Статус: PENDING.";


    } // end method orders PostMapping


} //  end class OrderController
