package com.doitteam.foodstore.repository;

import com.doitteam.foodstore.model.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory, Long> {

    List<OrderStatusHistory> findByOrderId(Long orderId);

    @Query("SELECT osh FROM OrderStatusHistory osh WHERE osh.order.id = :orderId ORDER BY osh.createdAt DESC")
    List<OrderStatusHistory> findByOrderIdOrderByCreatedAtDesc(@Param("orderId") Long orderId);
}