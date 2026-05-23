package br.com.maiawall.finances;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "GEMINI_API_KEY", matches = ".+")
public class ChatClientIT {

    @Autowired
    private ChatModel chatModel;

    @Test
    void testBasicChatResponse() {

        var chatClient = ChatClient.builder(chatModel)
                .defaultSystem("Você é um assistente matemático preciso.")
                .build();

        String response = chatClient.prompt()
                .user("Some 20 com 10 e retorne apenas o número")
                .call()
                .content();

        System.out.println("Response: " + response);

        // validação correta (flexível)
        assertTrue(response.contains("30"));
    }
}