package com.sarataza.atelieBot.Repository;

import com.sarataza.atelieBot.Model.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    List<OrderEntity> getOrderEntitiesByAppUserEntityId( Long id);
    Optional<OrderEntity> getOrderEntitiesByNumber(Integer number);
}
