package com.kostyan.fooddash.repository;

import com.kostyan.fooddash.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // СИНЬОРСКИЙ АВТО-МЕТОД: Спринг сам напишет SQL запрос SELECT * FROM orders WHERE user_id = ?
    List<Order> findByUserId(Long userId);
}
