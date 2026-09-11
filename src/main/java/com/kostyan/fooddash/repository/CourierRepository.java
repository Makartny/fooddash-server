package com.kostyan.fooddash.repository;

import com.kostyan.fooddash.model.Courier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Приказ Спрингу: «Положи этот пульт в свой сейф памяти!»
public interface CourierRepository extends JpaRepository<Courier, Long> {
    // Нам автоматически достаются кнопки .save(), .findById(), .findAll() и .deleteById()!
}
