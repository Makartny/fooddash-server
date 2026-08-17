package com.kostyan.fooddash.controller;

import com.kostyan.fooddash.model.Product;
import com.kostyan.fooddash.repository.ProductRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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


}
