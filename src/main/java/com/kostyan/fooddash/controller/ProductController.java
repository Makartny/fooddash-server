package com.kostyan.fooddash.controller;

import org.springframework.web.bind.annotation.*;
import com.kostyan.fooddash.model.Product;
import com.kostyan.fooddash.repository.ProductRepository;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
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
    } // end method updateProductPrice

    // =======================================================
    // БОЕВАЯ ЗАДАЧА №3: ЦЕНОВОЙ РАДАР (GET + ФИЛЬТР В ССЫЛКЕ)
    // =======================================================
    @GetMapping("/products/filter") // ловим GET запрос на адрес "/product/filter"
    public List<Product> filterProductByPrice(@RequestParam Double maxPrice) {
        // @RequestParam откусит из ссылки хвостик ?maxPrice=500 и запишет в переменную

        // ШАГ 1: Достаем вообще ВСЕ продукты, которые сейчас есть на складе в Докере
        List<Product> allProducts = productRepository.findAll();

        // ШАГ 2: Создаем новый пустой список, куда будем складывать только дешевую еду
        ArrayList<Product> filteredList = new ArrayList<>();

        // ШАГ 3: Запускаем цикл for-each по всем продуктам из базы данных
        for (Product p : allProducts) {
            // Если цена конкретного блюда МЕНЬШЕ или РАВНА нашей планке maxPrice
            if (p.getPrice() <= maxPrice) {
                filteredList.add(p);// Бережно переносим этот продукт в наш отфильтрованный список!
            }
        } // end forEach

        // ШАГ 4: Возвращаем этот список в Postman! Спринг сам превратит его в красивый JSON-массив!
        return filteredList;
    }

    // =======================================================
    // БОЕВАЯ ЗАДАЧА №5: КАПКАН НА БУКВЫ (ЗАЩИТА СЕТЕВОГО ID)
    // =======================================================
    @GetMapping("/products/search-safe") // Ловим GET-запрос
    public String findProductsSafe(@RequestParam String id) {
        // 🛑 ХАКЕРСКИЙ ХОД: Принимаем id как строку String, чтобы сервер не упал при виде букв!

        // ШАГ 1: Проверяем строку через регулярное выражение.
        // Символы "\\d+" означают: «Внутри строки должны быть ТОЛЬКО цифры от 0 до 9!»
        if (!id.matches("\\d+")) {
            return "❌ Ошибка: Взломать не получится! Переданный ID '" + id
                    + "' содержит буквы или спецсимволы. Разрешены только цифры!";
        } // end if

        // ШАГ 2: Если проверка пройдена, мы со спокойной душой превращаем текст в число Long!
        Long numericId = Long.parseLong(id);

        // ШАГ 3: Ищем продукт в Докере через наш стандартный пульт
        Optional<Product> productFromDb = productRepository.findById(numericId);

        if (productFromDb.isEmpty()) {
            return "🔎 Продукт с ID " + numericId + " не найден на складе Докера.";
        } // end if

        return "🎯 Успех! Найден продукт: '" + productFromDb.get().getName() +
                "' за " + productFromDb.get().getPrice() + " руб.";
    }


} // end class
