package br.com.maiawall.finances.infra.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

@RestController
@RequestMapping("/api")
public class AudioResponseController {

        private final ChatClient chatClient;
        private final ObjectMapper objectMapper = new ObjectMapper();
        private final HttpClient httpClient = HttpClient.newHttpClient();

        @Value("${app.google.tts.api-key}")
        private String ttsApiKey;

        public AudioResponseController(ChatClient chatClient) {
                this.chatClient = chatClient;
        }

        @PostMapping(value = "/chat", consumes = MediaType.APPLICATION_JSON_VALUE, produces = "audio/mpeg")
        public ResponseEntity<ByteArrayResource> sinthesize(@RequestBody SynthesizeRequest request) throws Exception {

                /*
                 * String textoResposta = chatClient
                 * .prompt()
                 * .system("Você é um assistente financeiro. Responda as perguntas do usuário de forma clara e concisa em português. Não mencione nada sobre geração de áudio, apenas responda a pergunta normalmente."
                 * )
                 * .user(request.text())
                 * .call()
                 * .content();
                 */
                // byte[] audio = convertTextToSpeech(textoResposta); // Para usar a resposta do
                // chat como áudio

                byte[] audio = convertTextToSpeech(request.text());

                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                                ContentDisposition.attachment().filename("audio.mp3").build()
                                                                .toString())
                                .body(new ByteArrayResource(audio));
        }

        private byte[] convertTextToSpeech(String texto) throws Exception {
                ObjectNode body = objectMapper.createObjectNode();
                body.putObject("input").put("text", texto);
                body.putObject("voice").put("languageCode", "pt-BR").put("name", "pt-BR-Standard-B");
                body.putObject("audioConfig").put("audioEncoding", "MP3");

                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create("https://texttospeech.googleapis.com/v1/text:synthesize?key="
                                                + ttsApiKey))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                                .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                        throw new RuntimeException(
                                        "Falha na API TTS. Status: " + response.statusCode() + " | " + response.body());
                }

                String audioBase64 = objectMapper.readTree(response.body()).get("audioContent").asText();
                return Base64.getDecoder().decode(audioBase64);
        }

        public record SynthesizeRequest(String text) {
        }
}