package com.kostyan.fooddash.model;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false)
    private double price;

    private String description;

    // Пустой конструктор, обязательный для Hibernate
   public Product() {
    }

    // Удобный конструктор для создания блюд
   public Product(String name, double price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }
    // Геттеры и сеттеры (чтобы Java могла читать и менять данные)

    //====== ID
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    //======= Name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //====== Price
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    //====== Description
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


} // end Class Product
