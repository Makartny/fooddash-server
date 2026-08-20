package com.kostyan.fooddash.controller;

import com.kostyan.fooddash.model.Product;
import com.kostyan.fooddash.repository.ProductRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    // ==========================================
    // ВХОДЯЩИЕ ВОРОТА: ПРИЕМ И СОХРАНЕНИЕ НОВОГО БЛЮДА
    // ==========================================
    @PostMapping("/products")
    public Product createProduct(@RequestBody Product newProduct) {
        // Убедись, что 'newProduct' тут и ниже написаны ОДИНАКОВО (с большой буквы P в середине)!
        System.out.println("📥 На сервер прилетел новый продукт: " + newProduct.getName());

        return productRepository.save(newProduct);
    }


}
