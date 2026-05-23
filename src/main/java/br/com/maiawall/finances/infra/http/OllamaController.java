package br.com.maiawall.finances.infra.http;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
public class OllamaController {

    private OllamaChatModel chatModel;

    public OllamaController(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/ask/{question}")
    public String ask(@PathVariable String question) {

        return ChatClient.create(chatModel)
                .prompt(question)
                .call()
                .content();
    }

}
