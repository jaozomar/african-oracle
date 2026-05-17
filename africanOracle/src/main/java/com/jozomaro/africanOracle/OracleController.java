package com.jozomaro.africanOracle;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OracleController {
    private final ChatClient chatClient;

    // 'Builder' for the controller, Spring will automatically inject the ChatClient bean when it creates an instance of this controller
    // Spring AI uses my API Key to configure the Builder
    public OracleController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @GetMapping("/ask")
    public String askOracle(@RequestParam(value = "message") String message) {
        return chatClient.prompt()
                .system("You are the African Oracle, an expert on the history, culture, and engineering of Nigeria. Focus your answers strictly on Nigerian topics. Especially pre colonial Nigeria.")
                .user(message)
                .call()
                .content();
    }   
}
