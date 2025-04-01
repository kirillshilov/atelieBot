package com.sarataza.atelieBot.Model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Data
public class AppUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long login;
    private String firstName;
    private String lastName;
    private String phone;
    private String state;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "appUserEntity")
    @ToString.Exclude
    private List<OrderEntity> orderList = new ArrayList<>();
}
