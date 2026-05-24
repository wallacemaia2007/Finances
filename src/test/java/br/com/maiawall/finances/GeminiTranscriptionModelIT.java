package br.com.maiawall.finances;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "GEMINI_API_KEY", matches = ".+")
public class GeminiTranscriptionModelIT {

    @Autowired
    private ChatClient chatClient;

    @ParameterizedTest
    @CsvSource({
            "audio/audio_1.mp3"
    })
    void shouldTranslateAudio() {

        var recording = new ClassPathResource("audio/audio_1.mp3");

        Media audioMedia = new Media(
                MimeTypeUtils.parseMimeType("audio/mpeg"),
                recording);

        UserMessage userMessage = UserMessage.builder()
                .text("""
                        Transcreva o áudio.
                        Depois traduza para inglês.
                        Retorne apenas a tradução.
                        """)
                .media(audioMedia)
                .build();

        String response = chatClient.prompt()
                .messages(userMessage)
                .call()
                .content();

        System.out.println(response);

        assertTrue(response != null && !response.isBlank());
    }
}