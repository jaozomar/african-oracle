package com.jozomaro.africanOracle.service;

import com.jozomaro.africanOracle.model.Analytics;
import com.jozomaro.africanOracle.repository.ArtifactRepository;

import java.util.*;

import org.springframework.stereotype.Service;

@Service
public class AnalyticsService {

    private final ArtifactRepository artifactRepository;

    public AnalyticsService(ArtifactRepository artifactRepository) {
        this.artifactRepository = artifactRepository;
    }

    public List<Analytics> getCleanedMaterialAnalytics() {
        Map<String,Long> materialCounts = new HashMap<>(); // empty map for clean data

        List<Analytics> rawStats = artifactRepository.countArtifactsByMaterial(); // raw data

        for(Analytics stat: rawStats) {
            // each Analytic has a label and count
            String messyLabel = stat.label(); // e.g. "wood; fibre", etc
            Long count = stat.count(); // e.g. 10, etc

            String[] separateMaterials = messyLabel.split(";");

            for(String material : separateMaterials) {
                String cleanMaterial = material.trim(); // get rid of leading/trailing spaces
                
                // if string ends with "(some extra text)", remove the parentheses content. Avoids redundancy.
                if (cleanMaterial.contains("(")) {
                    cleanMaterial = cleanMaterial.substring(0, cleanMaterial.indexOf("(")).trim();
                }
                if(cleanMaterial.isEmpty()) continue; // skip empty "materials"
                
                if(materialCounts.containsKey(cleanMaterial)) {
                    materialCounts.put(cleanMaterial, materialCounts.get(cleanMaterial) + count); // add count if there already is one
                } else {
                    materialCounts.put(cleanMaterial, count); // update with fresh count if adding material for the first time into map
                }
            }
        }

        List<Analytics> cleanedList = new ArrayList<>();
        for(Map.Entry<String, Long> entry: materialCounts.entrySet()) { // loop through every entry of the materialCounts map
            cleanedList.add(new Analytics(entry.getKey(), entry.getValue())); // add all the key value pairs into Analytics object
        }

        cleanedList.sort((a, b) ->Long.compare(b.count(), a.count())); // Sort by highest count to lowest count
        return cleanedList;
    }

    public List<Analytics> getCleanedPlaceAnalytics() {
        List<Analytics> rawStats = artifactRepository.countArtifactsByPlace();
        Map<String, Long> placeCounts = new HashMap<>();
        List<String> prefixes = Arrays.asList(

        );

        List<String> excludedPlaces = Arrays.asList(
            
        );

        for(Analytics stat : rawStats) {
            String messyPlace = stat.label();
            Long count = stat.count();
        }

        return placeCounts.entrySet().stream()
                .map(entry -> new Analytics(entry.getKey(), entry.getValue()))
                .toList();
    }
}