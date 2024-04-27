package com.sarataza.atelieBot.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@NoArgsConstructor
@Data
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long number;
    private String works;
    private String LocalDate;
    private Boolean done;
    @ManyToOne (cascade = CascadeType.REFRESH)
    @ToString.Exclude
    @JoinColumn(name = "appUserEntity_id")
    private AppUserEntity appUserEntity;
}
