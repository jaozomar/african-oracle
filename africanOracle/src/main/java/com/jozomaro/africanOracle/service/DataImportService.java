package com.jozomaro.africanOracle.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.stereotype.Service;

import com.jozomaro.africanOracle.repository.ArtifactRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service // "Let's know spring this is a specialist class"
@RequiredArgsConstructor // "Lombok, give me a constructor for the Repository"
public class DataImportService {
    private final ArtifactRepository repository;
    
    @PostConstruct // "Spring, run this the moment you finish setting me up"
    public void importData() {
        // get nigerian artifacts csv file
        InputStream nigerianArtifacts = getClass().getResourceAsStream("resources/nigeriaArtifacts.csv");
        

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(nigerianArtifacts))) {
            // can handle exception if reader fails to open
            String i; // i for input. Will hold input read from csv file

            // print out all lines of the csv file
            while( (i = reader.readLine()) != null) {
                System.out.println("Found: " + i);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Data has been read!");
    }
}