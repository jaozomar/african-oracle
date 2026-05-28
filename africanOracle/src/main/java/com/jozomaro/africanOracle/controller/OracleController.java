package com.jozomaro.africanOracle.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jozomaro.africanOracle.model.Artifact;
import com.jozomaro.africanOracle.repository.ArtifactRepository;
import com.jozomaro.africanOracle.service.AnalyticsService;
import com.jozomaro.africanOracle.model.Analytics;

@RestController
public class OracleController {
    private final ChatClient chatClient;
    private final ArtifactRepository artifactRepository; // Repository to access artifacts from the database
    private final AnalyticsService analyticsService;

    // 'Builder' for the controller, Spring will automatically inject the ChatClient bean when it creates an instance of this controller
    // Spring AI uses my API Key to configure the Builder
    public OracleController(ChatClient.Builder builder, ArtifactRepository artifactRepository, AnalyticsService analyticsService) {
        this.chatClient = builder.build();
        this.artifactRepository = artifactRepository;
        this.analyticsService = analyticsService;
    }

    @GetMapping("/ask")
    public String askOracle(@RequestParam(value = "message") String message) {
        List<Artifact> matchingArtifacts = artifactRepository.findTop5ByKeyword(message);

        String databaseContext = matchingArtifacts.stream()
        .limit(2) // Limit to top 2 artifacts due to Token constraints
                .map(a -> String.format("Type: %s\nMuseum No: %s\nTitle: %s\nDescription: %s\nPlace: %s\nMaterials: %s\n---", 
                        a.getType(), a.getMuseumNum(), a.getTitle(), a.getDescription(), a.getPlace(), a.getMaterials()))
                .collect(Collectors.joining("\n"));
        
        if(databaseContext.isBlank()) {
            databaseContext = "No relevant artifacts found in the database.";
        }

        return chatClient.prompt()
                .system("You are the African Oracle, an expert on the history, culture, and engineering of pre-colonialNigeria. " +
                        "Use the following database context to answer the user's question. If the context does not contain relevant information, answer based on your general knowledge. " +
                        "Prioritize using this data over your general knowledge when possible. " +
                        "\n\nDatabase Context:\n" + databaseContext)
                .user(message)
                .call()
                .content();
    }   

    @GetMapping("/api/analytics/materials")
    public List<Analytics> getMaterialsAnalytics() {
        return analyticsService.getCleanedMaterialAnalytics();
    }
}
