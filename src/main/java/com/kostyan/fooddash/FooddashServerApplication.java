package com.kostyan.fooddash;

import com.kostyan.fooddash.model.Product;
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
    public CommandLineRunner initDatabase(ProductRepository repository) {
        return args -> {
            // Проверяем: если база пока пустая, закидываем стартовое меню
            if (repository.count() == 0) {
                System.out.println("🚀 Наполняем базу данных стартовыми вкусняшками...");
                repository.save(new Product("Пицца Пепперони", 599.00, "Острая пицца с колбасками пепперони и сыром моцарелла"));
                repository.save(new Product("Бургер Шеф", 380.50, "Сочная котлета из мраморной говядины с фирменным соусом"));
                System.out.println("✅ Стартовое меню успешно сохранено в Докере!");
            }
        };
    }
}

