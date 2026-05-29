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

        return getCleanedList(materialCounts);
    }

    // Excludes photographs because the data often lists location of the photograph, not location of object photographed
    public List<Analytics> getCleanedPlaceAnalytics() {
        List<Analytics> rawStats = artifactRepository.countArtifactsByPlace();
        Map<String, Long> placeCounts = new HashMap<>();
        
        // Known prefixes found in data, sorted by highest priority to lowest
        List<String> prefixes = Arrays.asList(
            "Original from:",
            "Issued in:",
            "Made in:",
            "Claimed to be from:",
            "Published in:",
            "Painted in:",
            "Printed in:"
        );

        // Known outlier locations in data that we want to exclude
        List<String> excludedPlaces = Arrays.asList(
            "Spain",
            "London",
            "Birmingham",
            "Lisbon",
            "USA",
            "Sri Lanka",
            "Vienna",
            "Africa", // Too Broad
            "Europe",
            "Government House", // No context
            "Northern Region", // No context
            "Benue Valley", // Too Broad
            "Asante Region", // Already have "Ghana", and is outside Nigeria
            "Rhodesia" // Outside Nigeria and was a colonial state
        );

        for(Analytics stat : rawStats) {
            String messyPlace = stat.label();
            Long count = stat.count();

            String[] separatePlaces = messyPlace.split(";"); // Separate separate locations given for an Artifact
            String truePlace = ""; // The true place of origin

            // Search for prefixes
            for(String prefix : prefixes) {
                for(String place : separatePlaces) {
                    if(place.contains(prefix)) {
                        String cleanPlace = place.replace(prefix, "").trim(); // trim leading/trailing spaces, and remove prefix
                        
                        // if string ends with "(some extra text)", remove the parentheses content. Avoids redundancy.
                        if (cleanPlace.contains("(")) {
                            cleanPlace = cleanPlace.substring(0, cleanPlace.indexOf("(")).trim();
                        }

                        // Exclude any modern museum origins. We want the true place of origin
                        // Also skip empty "places"
                        if(!cleanPlace.toLowerCase().contains("museum") && !cleanPlace.isEmpty()) {
                            truePlace = cleanPlace;
                            break;
                        }
                    }
                }
                if(!truePlace.isEmpty()) break; // If truePlace is not empty, then we already have our location. Otherwise, onto the next prefix
            }

            if(truePlace.isEmpty()) { // FallBack for records without any prefix
                String fallBack = messyPlace.trim();

                if(fallBack.contains("(")) fallBack.substring(0, fallBack.indexOf("(")).trim();


                if(!fallBack.toLowerCase().contains("museum") &&
                   !fallBack.toLowerCase().contains("photographed in") && // skip photographs 
                   !fallBack.toLowerCase().contains("macedonia")) //TARGETED FIX: There is a macedonian entry that has location "Ake", which shares the name with a Nigerian location
                   truePlace = fallBack;
            }

            // If true place is empty or an excluded location, then skip
            if(truePlace.isEmpty() || excludedPlaces.contains(truePlace)) continue;
            // Check if truePlace is already in the map
            if(placeCounts.containsKey(truePlace)) 
                placeCounts.put(truePlace, placeCounts.get(truePlace) + count); // add count if there already is one
            else 
                placeCounts.put(truePlace, count); // update with fresh count if adding place for the first time into map

        }
        
        return getCleanedList(placeCounts);
    }

    private List<Analytics> getCleanedList(Map<String, Long> inMap) {
        List<Analytics> cleanedList = new ArrayList<>();
        for(Map.Entry<String, Long> entry: inMap.entrySet()) { // loop through every entry of the materialCounts map
            cleanedList.add(new Analytics(entry.getKey(), entry.getValue())); // add all the key value pairs into Analytics object
        }

        cleanedList.sort((a, b) ->Long.compare(b.count(), a.count())); // Sort by highest count to lowest count
        return cleanedList;
    }
}