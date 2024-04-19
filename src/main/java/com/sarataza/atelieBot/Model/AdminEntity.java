package com.sarataza.atelieBot.Model;

import jakarta.persistence.*;

import lombok.*;


@Entity
@NoArgsConstructor
@Data
public class AdminEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NonNull
    private Long login;
    private String state;
}

