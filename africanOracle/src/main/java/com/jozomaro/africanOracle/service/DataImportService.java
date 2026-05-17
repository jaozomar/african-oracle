package com.jozomaro.africanOracle.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.stereotype.Service;

import com.jozomaro.africanOracle.model.Artifact;
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
        //InputStream nigerianArtifacts = getClass().getResourceAsStream("/nigeriaArtifacts.csv");
        

        try (InputStream is = getClass().getResourceAsStream("/nigeriaArtifacts.csv");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is));
             com.opencsv.CSVReader csvReader = new com.opencsv.CSVReader(reader)) {
            
           
            String[] data; // will store the data from the csv
            int count = 0;

            csvReader.readNext(); // Skip the header row

            
            while( (data = csvReader.readNext()) != null) {
                Artifact artifact = new Artifact();
                
                artifact.setType(data[1]);
                artifact.setMuseumNum(data[2]);
                artifact.setTitle(data[3]);
                artifact.setDescription(data[6]);
                artifact.setPlace(data[15]);
                artifact.setMaterials(data[17]);

                repository.save(artifact); //save to repository

                count++;
                if (count % 100 == 0) System.out.println("Saved " + count +" artifacts!");
                
            }
            
            System.out.println("FINISHED READING!!!! Total Artifacts Saved: " + count);

        } catch (Exception e) { // can handle exception if reader fails to open
            e.printStackTrace();
        }

        //System.out.println("Data has been read!");
    }
}