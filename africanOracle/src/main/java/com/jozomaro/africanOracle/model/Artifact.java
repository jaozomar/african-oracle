package com.jozomaro.africanOracle.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Artifact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // Artifact Type
    private String museumNum; // Artifact Number
    private String title; // Artifact Title

    @Column(columnDefinition = "TEXT") // To Accomodate for long descriptions. Handles unlimited text length
    private String description; // Artifact Description

    private String place; // Artifact Production Place
    private String materials; // Artifact Materials
}
