package com.kostyan.fooddash.controller;

import com.kostyan.fooddash.model.Order;
import com.kostyan.fooddash.model.OrderItem;
import com.kostyan.fooddash.model.User;
import com.kostyan.fooddash.repository.OrderItemRepository;
import com.kostyan.fooddash.repository.OrderRepository;
import com.kostyan.fooddash.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

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
    } // end createOrder

    // =======================================================
    // БОЕВАЯ ЗАДАЧА №7: ТОТАЛЬНОЕ ОЧИЩЕНИЕ (DELETE ЗАКАЗОВ ЮЗЕРА)
    // =======================================================

    @DeleteMapping("/orders/clear-history") // Ловим DELETE-запрос на адрес /orders/clear-history
    public String clearUserOrderHistory(@RequestParam Long userId) {
        // @RequestParam откусит цифровой ID пользователя прямо из ссылки (?userId=2)

        // ШАГ 1: Используем наш кастомный метод из OrderRepository, который мы писали в Блоке 16!
        // Он сделает SQL-запрос: SELECT * FROM orders WHERE user_id = userId;
        List<Order> userOrders = orderRepository.findByUserId(userId);

        // ШАГ 2: Защитный капкан! Если у пользователя и так нет ни одного заказа — выходим!
        if (userOrders.isEmpty()) {
            return "🔎 Хьюстон, у нас чисто! У пользователя с ID " + userId + " история заказов и так абсолютно пустая.";
        } // end if

        // ШАГ 3: Запоминаем количество для красивого админского отчета
        int countToDelete = userOrders.size();

        // 🛑 ЭТАЖ 1: ЗАЧИСТКА ВТОРОСТЕННЫХ ТАБЛИЦ (order_items)
        // Пробегаем циклом по каждому чеку пользователя
        for (Order currentOrder : userOrders) {
            // Через наш третий пульт orderItemRepository ищем ВСЮ еду, привязанную к этому конкретному чеку
            List<OrderItem> items = orderItemRepository.findByOrderId(currentOrder.getId());

            // На корню стираем еду из таблицы order_items! Докер доволен — сирот не останется!
            orderItemRepository.deleteAll(items);
        } // end forEach


        // ШАГ 4: Нажимаем ядерную кнопку .deleteAll() на пульте orderRepository
        // Спринг сам сгенерирует SQL-команды DELETE и сотрет все эти чеки из Докера разом!
        orderRepository.deleteAll(userOrders);

        return "🗑️ Операция 'Полное очищение' завершена! Из базы данных Докера успешно удалено заказов: "
                + countToDelete + " шт. для пользователя с ID " + userId + ".";


    } // end clearUserOrderHistory

    // =======================================================
    // БОЕВАЯ ЗАДАЧА №8: КУРЬЕРСКИЙ МОСТИК (СМЕНА СТАТУСА ЧЕКА)
    // =======================================================
    @PutMapping("/orders/update-status")
    public String updateOrderStatus(@RequestParam Long orderId, @RequestParam String newStatus) {
        // В параметрах метода забираем из ссылки id чека и новый статус

        // ШАГ 1: Ищем сам заказ в Докере через пульт orderRepository
        Optional<Order> orderFromDb = orderRepository.findById(orderId);

        // ШАГ 2: Защитный капкан! Если чека с таким ID нет — рубим операцию!
        if (orderFromDb.isEmpty()) {
            return "❌ Ошибка: Заказ №" + orderId + " не найден в базе данных Докера!";
        }

        // Если программа прошла проверку положительно записываем в переменную этот чек
        Order realOrder = orderFromDb.get();

        // ШАГ 3: ЖЁСТКИЙ БИЗНЕС-ПРЕДОХРАНИТЕЛЬ!
        // Если заказ УЖЕ доставлен, блокируем любые попытки курьера изменить его состояние!
        if ("DELIVERED".equals(realOrder.getStatus())) {
            return "🛑 Стоп-машина! Заказ №" + orderId + " уже был успешно доставлен клиенту." +
                    " Статус заблокирован от изменений!";
        } // end if

        // ШАГ 4: Запоминаем старый статус для красивого отчета курьеру
        String oldStatus = realOrder.getStatus();

        // ШАГ 5: Переписываем ячейку статуса в оперативной памяти Java
        realOrder.setStatus(newStatus.toUpperCase());

        // ШАГ 6: Нажимаем кнопку .save(). Hibernate просто обновит колонку status в Докере!
        orderRepository.save(realOrder);

        return "🚚 Курьер-офис: Статус заказа №" + orderId + " успешно изменён!" +
                " Было: " + oldStatus + " ➡️ Стало: " + realOrder.getStatus() + ".";

    } // end updateOrderStatus

    // =======================================================
    // БОЕВАЯ ЗАДАЧА №9: КАЛЬКУЛЯТОР СКИДОК И ПРОМОКОД (POST)
    // =======================================================
    @PostMapping("/orders/with-promo") // Ловим POST-запрос на оформление чека с промокодом
    public String createOrderWithPromo(@RequestBody List<OrderItem> basket, // Извлекаем корзину из грузового отсека JSON
                                       // (наш массив!)
                                       @RequestParam String username, // Извлекаем логин из ссылки
                                       @RequestParam String promo // Извлекаем текст промокода из ссылки
    ) {
        // ШАГ 1: Ищем покупателя в Докере через пульт проверки юзеров
        Optional<User> userFromDb = userRepository.findByUsername(username);
        if (userFromDb.isEmpty()) {
            return "❌ Ошибка: Заказ отклонен! Пользователь '" + username + "' не зарегистрирован!";
        } // end if

        User realUser = userFromDb.get();

        // ШАГ 2: Создаем пустой общий чек (Заказ) в оперативной памяти Java
        Order newOrder = new Order();
        newOrder.setUser(realUser); // Привязываем хозяина чека по его главному ключу!
        newOrder.setStatus("PENDING"); // Стартовый статус

        // ШАГ 3: МАТЕМАТИЧЕСКИЙ РАСЧЁТ СУММЫ БЛЮД КОРЗИНЫ
        Double finalPrice = 0.0;
        for (OrderItem item : basket) {
            // Наш стандартный защитный капкан от отрицательного количества!
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                return "❌ Ошибка! Количество товара не может быть меньше 1!";
            } // end if
            finalPrice += item.getProduct().getPrice() * item.getQuantity();
        } // end forEach


        // ШАГ 4: АВТОМАТИЧЕСКАЯ ПРОВЕРКА ПРОМОКОДА НА БЭКЕНДЕ
        // Метод .equalsIgnoreCase() сравнивает текст, игнорируя большие или маленькие буквы!
        boolean isPromoValid = "SPRING2026".equalsIgnoreCase(promo);
        String promoReport = "Промокод не применен.";
        if (isPromoValid) {
            Double discount = finalPrice * 0.20; // Высчитываем 20% скидки
            finalPrice = finalPrice - discount;  // Срезаем сумму чека!
            promoReport = "🎉 Применен промокод SPRING2026! Получена скидка 20% (-" + discount + " руб.)";
        }
        newOrder.setTotalPrice(finalPrice); // Запечатываем итоговую цену в чек!


        // ШАГ 5: Сохраняем ОБЩИЙ ЧЕК в Докер, чтобы сгенерировался уникальный ID!
        Order savedOrder = orderRepository.save(newOrder);

        // ШАГ 6: Раклыдываем еду по полочкам в таблицу order_items
        for (OrderItem item : basket) {
            item.setOrder(savedOrder); // Привязываем еду к свежему ID чека
            orderItemRepository.save(item); // Уносим строчку в Докер
        }

        return "🛒 Корзина успешно обработана! Создан общий заказ №" + savedOrder.getId() +
                ". Итоговая сумма к оплате: " + savedOrder.getTotalPrice() + " руб. [" + promoReport + "].";

    } // end createOrderWithPromo

    // =======================================================
    // БОЕВАЯ ЗАДАЧА №10: ТОТАЛЬНЫЙ АУДИТ (ВЫРУЧКА РЕСТОРАНА)
    // =======================================================
    @GetMapping("/orders/stats") // Ловим GET-запрос на адрес /orders/stats
    public String getRestaurantStats() {
        // ШАГ 1: Извлекаем вообще ВСЕ заказы из таблицы orders Докера
        List<Order> allOrders = orderRepository.findAll();

        // ШАГ 2: Защитный капкан! Если ресторан новый и заказов ещё нет — отчитываемся сразу
        if (allOrders.isEmpty()) {
            return "📊 Отчёт FoodDash: В системе пока нет ни одного заказа. Выручка: 0.0 руб.";
        } // end if

        // ШАГ 3: Заводим счётчики в оперативной памяти Java Core
        int totalOrdersCount = allOrders.size(); // Узнаем общее количество чеков
        Double totalRevenue = 0.0;  // Сюда будем суммировать деньги

        // ШАГ 4: Конвейер-переборщик: бежим по всем чекам и складываем их финальные цены
        for (Order o : allOrders) {
            totalRevenue += o.getTotalPrice();
        } // end forEach

        // ШАГ 5: Возвращаем красивый, структурированный текстовый отчёт директору!
        return "📊 ГЛУБОКИЙ АУДИТ РЕСТОРАНА FOODDASH:\n" +
                "--------------------------------------\n" +
                "📦 Общее количество оформленных заказов: " + totalOrdersCount + " шт.\n" +
                "💰 Тотальная выручка на жёстком диске Докера: " + totalRevenue + " руб.\n" +
                "🚀 Бэкенд-система работает в штатном режиме!";

    } // end getRestaurantStats


} // end class OrderController
