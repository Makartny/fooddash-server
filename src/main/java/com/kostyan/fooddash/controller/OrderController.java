package com.kostyan.fooddash.controller;

import com.kostyan.fooddash.model.Order;
import com.kostyan.fooddash.model.OrderItem;
import com.kostyan.fooddash.model.User;
import com.kostyan.fooddash.repository.OrderItemRepository;
import com.kostyan.fooddash.repository.OrderRepository;
import com.kostyan.fooddash.repository.UserRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController // Слушаем интернет на порту 8080
public class OrderController {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository; // Наш третий пульт для корзины

    // Конструктор: Спринг сам выдаёт нам в руки три этих инструмента из своего сейфа
    public OrderController(OrderRepository orderRepository, UserRepository userRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
    }

    // ===================================================
    // ЦЕНТРАЛЬНЫЙ УЗЕЛ: ОФОРМЛЕНИЕ ПОЛНОЦЕННОЙ КОРЗИНЫ
    // ===================================================
    @PostMapping("/orders")
    public String createOrder(@RequestBody List<OrderItem> basket, @RequestParam String username) {
        // 1. Из ссылки вытаскиваем логин и проверяем, есть ли такой покупатель в Докере
        Optional<User> userFromDb = userRepository.findByUsername(username);
        if (userFromDb.isEmpty()) {
            return "❌ Ошибка: Заказ отклонен! Пользователь '" + username + "' не зарегистрирован!";
        }
        User realUser = userFromDb.get();

        // 2. Сколотим в оперативной памяти пустой общий чек (Заказ)
        Order newOrder = new Order();
        newOrder.setUser(realUser); // Привязываем хозяина чека по его главному ключу!
        newOrder.setStatus("PENDING"); // Выставляем стартовый статус

        // 3. АВТОМАТИЧЕСКИЙ РАСЧЁТ СУММЫ ЗАКАЗА НА БЭКЕНДЕ
        Double finalPrice = 0.0;
        for (OrderItem item : basket) {
            // 🛑 НАШ НОВЫЙ ПРЕДОХРАНИТЕЛЬ:
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                return "❌ Ошибка: Оформление отклонено! Количество товара не может быть меньше 1 или пустым!";
            }
            // Бежим по корзине: берём цену продукта и умножаем на его количество
            finalPrice += item.getProduct().getPrice() * item.getQuantity();
        }
        newOrder.setTotalPrice(finalPrice); // Записываем итоговую сумму в чек

        // 4. Записываем ОБЩИЙ ЧЕК в Докер, чтобы база сгенерировала для него уникальный ID!
        Order savedOrder = orderRepository.save(newOrder);

        // 5. РАСКЛАДЫВАЕМ ЕДУ ПО ПОЛОЧКАМ В ПЯТУЮ ТАБЛИЦУ (order_items)
        for (OrderItem item : basket) {
            item.setOrder(savedOrder); // Намертво привязываем этот кусочек еды к нашему созданному чеку!
            orderItemRepository.save(item); // Уносим строчку в таблицу order_items Докера
        }

        return "🛒 Корзина успешно обработана! Создан общий заказ №" + savedOrder.getId() +
                ". Итоговая сумма: " + savedOrder.getTotalPrice() + " руб. Статус: PENDING.";
    }
}
