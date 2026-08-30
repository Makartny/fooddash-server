package com.kostyan.fooddash.model;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items") // Создаём в Докере физическую таблицу 'order_items'
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;  // Количество конкретного блюда в заказе (например, 2 штуки)

    // ==========================================
    // СВЯЗУЮЩИЙ МОСТ №1: КАКОЕ БЛЮДО ЗАКАЗАЛИ?
    // ==========================================
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false) // Создаёт колонку product_id, привязанную к главному ID продукта
    private Product product;

    // ==========================================
    // СВЯЗУЮЩИЙ МОСТ №2: К КАКОМУ ЧЕКУ ОТНОСИТСЯ?
    // ==========================================
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false) // Создаёт колонку order_id, привязанную к главному ID заказа
    private Order order;

    // Пустой конструктор для Hibernate
    public OrderItem() {
    }

    // Конструктор для быстрого создания элемента в коде
    public OrderItem(Integer quantity, Product product, Order order) {
        this.quantity = quantity;
        this.product = product;
        this.order = order;
    }

    // Блок Геттеров и Сеттеров (наши стандартные провода)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }


} // end class OrderItem
