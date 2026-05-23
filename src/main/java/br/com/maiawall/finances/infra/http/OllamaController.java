package br.com.maiawall.finances.infra.http;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class OllamaController {

    private final ChatClient chatClient;

    public OllamaController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @GetMapping("/ask/{question}")
    public String ask(@PathVariable String question) {

        return chatClient
                .prompt()
                .system("You are a helpful assistant for financial questions.")
                .user(question)
                .call()
                .content();
    }
}