package com.kostyan.fooddash.controller;

import com.kostyan.fooddash.model.Courier;
import com.kostyan.fooddash.repository.CourierRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController // Приказ Спрингу: «Этот класс слушает курьерский веб-трафик!»
@RequestMapping("/couriers") // Авто-префикс: все ручки этого класса будут начинаться с /couriers
public class CourierController {

    private final CourierRepository courierRepository;

    // Внедряем пульт репозитория через конструктор
    public CourierController(CourierRepository courierRepository) {
        this.courierRepository = courierRepository;
    }

    // =======================================================
    // 🧱 РУЧКА №1: ДОБАВЛЕНИЕ НОВОГО КУРЬЕРА В СИСТЕМУ (POST)
    // =======================================================
    @PostMapping
    public String createCourier(@RequestBody Courier newCourier) {
        // @RequestBody заставит Jackson вскрыть JSON-пакет и собрать объект курьера

        // Нажимаем .save() на пульте репозитория. Hibernate уносит строку в Докер!
        Courier savedCourier = courierRepository.save(newCourier);

        return "🚚 Курьер-офис: Новый доставщик '" + savedCourier.getName() +
                "' успешно внесен в базу! Ему присвоен ID: " + savedCourier.getId();
    } // end method createCourier

    // =======================================================
    // 🧱 РУЧКА №2: ПРОСМОТР ВСЕХ КУРЬЕРОВ В ШТАТЕ (GET)
    // =======================================================
    @GetMapping
    public List<Courier> getAllCouriers() {
        // Просто вытаскиваем из Докера весь список курьеров в сеть
        return courierRepository.findAll();
    } // end GetAllCouriers




} // end class CourierController
