package br.com.maiawall.finances.infra.http;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.http.MediaType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class TranscriptionController {

    private final ChatClient chatClient;

    public TranscriptionController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String transcribe(
            @RequestParam("file") MultipartFile file) {

        Media audioMedia = new Media(
                MimeTypeUtils.parseMimeType(file.getContentType()),
                file.getResource());

        UserMessage userMessage = UserMessage.builder()
                .text("""
                        Transcreva o áudio para português.
                        Retorne apenas a transcrição.
                        """)
                .media(audioMedia)
                .build();

        String response = chatClient.prompt()
                .messages(userMessage)
                .call()
                .content();

        return response;
    }
}