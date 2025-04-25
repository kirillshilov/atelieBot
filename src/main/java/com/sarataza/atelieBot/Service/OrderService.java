package com.sarataza.atelieBot.Service;

import com.sarataza.atelieBot.Model.OrderEntity;
import com.sarataza.atelieBot.Repository.OrderRepository;
import com.sarataza.atelieBot.Service.User.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;
    public String getOrderByStringForUser(List<OrderEntity> orderList){
        StringBuilder stringBuilder = new StringBuilder();
        for (OrderEntity order: orderList) {
            stringBuilder.append("№ " + order.getNumber()+  "\n"  + " Работы: " + order.getWorks() +  "\n" + " Готовность: " + order.getDone()+  "\n" + "\n");
        }
        String finalText = stringBuilder.toString();
        finalText = finalText.replace("true", " готов ");
        finalText = finalText.replace("false", " не готов ");
        finalText = finalText.replace("null", "нет");
        return finalText;
    }
    public String getOrderByStringForAdmin(List<OrderEntity> orderList){
        StringBuilder stringBuilder = new StringBuilder();
        for (OrderEntity order: orderList) {
            stringBuilder.append("№ " + order.getNumber()+  "\n"  +
                    " Работы: " + order.getWorks() + "\n" +
                    " Время приема: "+ order.getLocalDate() +"\n"
            );
            if(order.getAppUserEntity() != null){
                stringBuilder.append(" Заказчик: " + userService.getUserToStringByIdForAdmin(order.getAppUserEntity().getId()) +  "\n" + "\n");
            }
            else{
                stringBuilder.append("\n" + "\n");
            }
        }
        String temp = stringBuilder.toString();
        temp = temp.replace("null", "нет");
        return temp;
    }

    public List<OrderEntity> getAllOrderByUserId(Long id) {
        return orderRepository.getOrderEntitiesByAppUserEntityId(id);
    }
    public List<OrderEntity> getAllActiveOrder() {
        return orderRepository.getOrderEntitiesByDoneFalse();
    }
    public Optional<OrderEntity> findOrderByNumber(Long number){
        return orderRepository.getOrderEntitiesByNumber(number);
    }
    public OrderEntity saveOrder(OrderEntity orderEntity){
        return orderRepository.save(orderEntity);
    }
}
