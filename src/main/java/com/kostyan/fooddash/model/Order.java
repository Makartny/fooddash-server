package com.kostyan.fooddash.model;

import jakarta.persistence.*;

@Entity
@Table(name = "orders") // Создаём в Докере отдельную физическую таблицу 'orders'
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double totalPrice; // Финальная сумма чека, которую посчитает бэкенд

    @Column(nullable = false, length = 20)
    private String status; // Статус заказа: PENDING (ожидает), COOKING (готовится), DELIVERED (доставлен)

    // ==========================================
    // СВЯЗУЮЩИЙ ЗАМОК: ПРИВЯЗКА ЗАКАЗА К КЛИЕНТУ
    // ==========================================

    @ManyToOne // Много заказов могут принадлежать ОДНОМУ пользователю
    @JoinColumn(name = "user_id", nullable = false)
    // Создаём в таблицеorders колонку 'user_id', которая не может быть пустой
    private User user; // Ссылка на живой объект пользователя, который сделал этот заказ

    // Пустой конструктор для Hibernate
    public Order() {
    }

    // Конструктор для быстрого создания заказа в коде
    public Order(Double totalPrice, String status, User user) {
        this.totalPrice = totalPrice;
        this.status = status;
        this.user = user;
    }

    // Блок Геттеров и Сеттеров (наши стандартные провода для передачи данных)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


} // end class Order
