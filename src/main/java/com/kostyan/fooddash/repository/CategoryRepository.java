package com.kostyan.fooddash.repository;

import com.kostyan.fooddash.model.Category; // Вот он — жесткий сквозной кабель до твоей модели!
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}
