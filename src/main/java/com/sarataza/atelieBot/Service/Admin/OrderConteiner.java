package com.sarataza.atelieBot.Service.Admin;

import com.sarataza.atelieBot.Model.OrderEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Data
@Slf4j
public class OrderConteiner {
    private OrderEntity staticOrder = new OrderEntity();
}
