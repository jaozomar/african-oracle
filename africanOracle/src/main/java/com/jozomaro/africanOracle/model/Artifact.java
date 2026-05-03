package com.jozomaro.africanOracle;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Artifact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String culture;
    private String material;

    @Column(length = 2000) // To Accomodate for long descriptions in data
    private String description;
}
