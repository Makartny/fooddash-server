package com.kostyan.fooddash.repository;

import com.kostyan.fooddash.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // СИНЬОРСКИЙ АВТО-МЕТОД: Спринг сам напишет SQL: SELECT * FROM order_items WHERE order_id = ?
    List<OrderItem> findByOrderId(Long orderId);
}
