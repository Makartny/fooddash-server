package com.kostyan.fooddash;

import com.kostyan.fooddash.model.Category;
import com.kostyan.fooddash.model.Product;
import com.kostyan.fooddash.repository.CategoryRepository;
import com.kostyan.fooddash.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FooddashServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FooddashServerApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDatabase(CategoryRepository categoryRepository, ProductRepository productRepository) {
        return args -> {
            // Если база полностью пустая, создаём связи с нуля
            if (categoryRepository.count() == 0 && productRepository.count() == 0) {
                System.out.println("🚀 Начинаем масштабное реляционное наполнение базы данных...");

                // 1. Создаем и сохраняем физические категории в Докере
                Category pizzaCategory = categoryRepository.save(new Category("Пицца"));
                Category burgerCategory = categoryRepository.save(new Category("Бургеры"));

                // 2. Создаем продукты и ЖЕСТКО привязываем к ним сохраненные категории через сеттер
                Product pizza = new Product("Пицца Пепперони", 599.00, "Острая пицца с колбасками пепперони и сыром моцарелла");
                pizza.setCategory(pizzaCategory); // Намертво вшиваем category_id = 1

                Product burger = new Product("Бургер Шеф", 380.50, "Сочная котлета из мраморной говядины с фирменным соусом");
                burger.setCategory(burgerCategory); // Намертво вшиваем category_id = 2

                // 3. Сохраняем продукты в базу данных
                productRepository.save(pizza);
                productRepository.save(burger);

                System.out.println("✅ Магия Many-to-One сработала! Категории и продукты связаны намертво!");
            } else System.out.println("В базе присутствуют данные ! Стартовый скрипт пропущен .");

        };
    }
}
