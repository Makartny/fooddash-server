package com.kostyan.fooddash.model;

import jakarta.persistence.*;

@Entity
@Table(name = "couriers") // В Докере создастся шестая таблица 'couriers'!
public class Courier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Автосчётчик ID (1, 2, 3...)
    private Long id;

    @Column(nullable = false)
    private String name;  // Имя курьера (например, "Иван", "Ахмед")

    @Column(nullable = false, unique = true)
    private String phone; // Телефон курьера (уникальный паспорт для связи)

    private String vehicleType; // Тип транспорта: "CAR" (машина), "BIKE" (велосипед), "FOOT" (пешком)

    // ==========================================
    // ГЕТТЕРЫ И СЕТТЕРЫ (Наши шлюзы для данных)
    // ==========================================
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }


} // end class Courier
