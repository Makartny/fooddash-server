package com.kostyan.fooddash.controller;

import org.springframework.web.bind.annotation.*;
import com.kostyan.fooddash.model.Product;
import com.kostyan.fooddash.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@RestController // Приказ Спрингу: «Этот класс слушает интернет-запросы!»
public class ProductController {

    private final ProductRepository productRepository;

    // Спринг сам зайдёт в свой сейф памяти и подставит сюда пульт 'productRepository'
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Когда пользователь перейдёт в браузере по ссылке /products — запустится этот метод!
    @GetMapping("/products")
    public List<Product> getAllProducts() {
        // Просто берём из репозитория ВСЕ блюда и возвращаем их прямо в сеть!
        return productRepository.findAll();
    }

    // ==========================================
    // ВХОДЯЩИЕ ВОРОТА: ПРИЕМ И СОХРАНЕНИЕ НОВОГО БЛЮДА
    // ==========================================
    @PostMapping("/products")
    public Product createProduct(@RequestBody Product newProduct) {
        // Убедись, что 'newProduct' тут и ниже написаны ОДИНАКОВО (с большой буквы P в середине)!
        System.out.println("📥 На сервер прилетел новый продукт: " + newProduct.getName());

        return productRepository.save(newProduct);
    }

    // ==========================================
    // БОЕВАЯ ЗАДАЧА №1: ОБНОВЛЕНИЕ ЦЕНЫ ПРОДУКТА
    // ==========================================

    @PutMapping("/products/update-price") // Ловим PUT-запрос благодаря нашему импорту!
    // Откусываем ID из ссылки  //  // Откусываем новую цену из ссылки
    public String updateProductPrice(@RequestParam Long id, @RequestParam Double newPrice) {
        // 1. Ищем продукт в Докере через наш встроенный пульт
        Optional<Product> productFromDb = productRepository.findById(id);
        // 2. Защитный капкан! Если продукта с таким ID нет — рубим операцию!
        if (productFromDb.isEmpty()) {
            return "❌ Ошибка: Продукта с ID " + id + " нет в меню FoodDash!";
        } // end if
        // 3. Достаем живой объект продукта из обертки Optional
        Product realProduct = productFromDb.get();
        // 4. Запоминаем старую цену для отчета
        Double oldPrice = realProduct.getPrice();
        // 5. Переписываем ценник в оперативной памяти Java
        realProduct.setPrice(newPrice);
        // 6. Нажимаем кнопку .save(). Hibernate сам поймет, что нужно ОБНОВИТЬ строку, а не создавать новую!
        productRepository.save(realProduct);
        return "💰 Цена продукта '" + realProduct.getName() +
                "' успешно изменена! Старая цена: " + oldPrice + " руб. Новая цена: " + newPrice + " руб.";
    }


} // end class
