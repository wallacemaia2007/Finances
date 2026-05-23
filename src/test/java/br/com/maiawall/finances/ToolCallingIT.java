package br.com.maiawall.finances;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "GEMINI_API_KEY", matches = ".+")
public class ToolCallingIT {

    @Autowired
    private ChatModel chatModel;

    static class CalculatorTool {

        @Tool(description = "Soma dois números")
        public int sum(int a, int b) {
            return a + b;
        }

        @Tool(description = "Subtrai dois números")
        public int subtract(int a, int b) {
            return a - b;
        }
    }

    @Test
    void testToolCalling() {

        var chatClient = ChatClient.builder(chatModel)
                .defaultSystem("""
                            Você é um assistente matemático.
                            Use ferramentas sempre que possível para cálculos.
                        """)
                .defaultTools(new CalculatorTool())
                .build();

        String response = chatClient.prompt()
                .user("Some 20 com 10 e retorne apenas o resultado")
                .call()
                .content();

        System.out.println("Response: " + response);

        // validação resiliente
        assertTrue(response.contains("30") || response.matches(".*30.*"));
    }
}