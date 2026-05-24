package br.com.maiawall.finances.application.usecase;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import br.com.maiawall.finances.application.usecase.input.SynthesizeAudioInput;
import br.com.maiawall.finances.application.usecase.output.SynthesizeAudioOutput;

@Service
public class SynthesizeAudioUseCase {

    private final ObjectMapper objectMapper = new ObjectMapper();;
    private final HttpClient httpClient;

    @Value("${app.google.tts.api-key}")
    private String ttsApiKey;

    public SynthesizeAudioUseCase() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public SynthesizeAudioOutput execute(SynthesizeAudioInput input) throws Exception {
        byte[] audio = convertTextToSpeech(input.text());
        return SynthesizeAudioOutput.from(audio);
    }

    private byte[] convertTextToSpeech(String text) throws Exception {
        ObjectNode body = objectMapper.createObjectNode();
        body.putObject("input").put("text", text);
        body.putObject("voice").put("languageCode", "pt-BR").put("name", "pt-BR-Standard-B");
        body.putObject("audioConfig").put("audioEncoding", "MP3");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://texttospeech.googleapis.com/v1/text:synthesize?key=" + ttsApiKey))
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
}
