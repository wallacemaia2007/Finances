package br.com.maiawall.finances;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Teste de integração: recebe áudio → transcreve/processa com Gemini → gera
 * áudio de resposta.
 *
 * Estratégia:
 * 1. Transcreve/processa o áudio de entrada usando Gemini (via Spring AI).
 * 2. Converte a resposta em texto para áudio via Google Cloud Text-to-Speech
 * REST API.
 *
 * Pré-requisitos:
 * - GEMINI_API_KEY: chave do Google AI Studio (já usada no projeto)
 * - Áudio de entrada em: src/test/resources/audio/audio_1.mp3
 *
 * O arquivo de saída gerado será salvo em: target/audio_response.mp3
 */
@SpringBootTest
@EnabledIfEnvironmentVariable(named = "GEMINI_API_KEY", matches = ".+")
public class GeminiAudioOutputIT {

        @Autowired
        private ChatClient chatClient;

        private static final String GEMINI_API_KEY = System.getenv("GEMINI_API_KEY");

        // -------------------------------------------------------------------------
        // Teste 1: Pipeline completo – áudio de entrada → texto → áudio de saída
        // -------------------------------------------------------------------------

        @Test
        void shouldProcessAudioAndReturnAudio() throws IOException, InterruptedException {

                // --- PASSO 1: Transcreve e processa o áudio de entrada com Gemini ---

                var recording = new ClassPathResource("audio/audio_1.mp3");

                Media audioMedia = new Media(
                                MimeTypeUtils.parseMimeType("audio/mpeg"),
                                recording);

                UserMessage userMessage = UserMessage.builder()
                                .text("""
                                                Você é um assistente financeiro.
                                                Escute o áudio e responda a pergunta do usuário de forma clara e concisa,
                                                em português, em no máximo 3 frases.
                                                """)
                                .media(audioMedia)
                                .build();

                String textoResposta = chatClient.prompt()
                                .messages(userMessage)
                                .call()
                                .content();

                System.out.println("=== Resposta em texto do Gemini ===");
                System.out.println(textoResposta);

                assertNotNull(textoResposta, "Gemini deve retornar uma resposta em texto");
                assertFalse(textoResposta.isBlank(), "A resposta não deve ser vazia");

                // --- PASSO 2: Converte o texto em áudio via Google Cloud TTS ---

                byte[] audioBytes = convertTextToSpeech(textoResposta);

                assertNotNull(audioBytes, "A API TTS deve retornar bytes de áudio");
                assertTrue(audioBytes.length > 0, "O áudio gerado não deve ser vazio");

                // Salva o áudio de saída para inspeção manual
                Path outputPath = Path.of("target/audio_response.mp3");
                Files.createDirectories(outputPath.getParent());
                try (var fos = new FileOutputStream(outputPath.toFile())) {
                        fos.write(audioBytes);
                }

                System.out.println("=== Áudio de resposta salvo em: " + outputPath.toAbsolutePath() + " ===");
                System.out.println("Tamanho do áudio: " + audioBytes.length + " bytes");
        }

        // -------------------------------------------------------------------------
        // Teste 2: Apenas TTS (útil para testar a geração de áudio isoladamente)
        // -------------------------------------------------------------------------

        @Test
        void shouldConvertTextToSpeechDirectly() throws IOException, InterruptedException {

                String texto = "Olá! Seu saldo atual é de dois mil reais. Você tem três transações pendentes este mês.";

                byte[] audioBytes = convertTextToSpeech(texto);

                assertNotNull(audioBytes, "A API TTS deve retornar bytes de áudio");
                assertTrue(audioBytes.length > 1000, "O áudio deve ter tamanho razoável");

                Path outputPath = Path.of("target/audio_tts_direto.mp3");
                Files.createDirectories(outputPath.getParent());
                Files.write(outputPath, audioBytes);

                System.out.println("=== Áudio TTS salvo em: " + outputPath.toAbsolutePath() + " ===");
        }

        // -------------------------------------------------------------------------
        // Helper: chama a Google Cloud TTS API via REST com a mesma GEMINI_API_KEY
        // -------------------------------------------------------------------------

        /**
         * Converte texto em áudio MP3 usando a Google Cloud Text-to-Speech API.
         *
         * Usa a mesma GEMINI_API_KEY do projeto (funciona com chaves do Google AI
         * Studio
         * que têm o Cloud TTS habilitado). Caso sua chave não tenha acesso ao TTS,
         * habilite a API em:
         * https://console.cloud.google.com/apis/library/texttospeech.googleapis.com
         *
         * Vozes disponíveis em pt-BR:
         * - pt-BR-Standard-A (feminina)
         * - pt-BR-Standard-B (masculina)
         * - pt-BR-Wavenet-A (feminina, mais natural – pode ter custo adicional)
         * - pt-BR-Wavenet-B (masculina, mais natural)
         */
        private byte[] convertTextToSpeech(String texto) throws IOException, InterruptedException {

                String ttsEndpoint = "https://texttospeech.googleapis.com/v1/text:synthesize?key=" + GEMINI_API_KEY;

                ObjectMapper mapper = new ObjectMapper();
                ObjectNode body = mapper.createObjectNode();

                // Texto de entrada
                ObjectNode input = mapper.createObjectNode();
                input.put("text", texto);
                body.set("input", input);

                // Configuração da voz em português
                ObjectNode voice = mapper.createObjectNode();
                voice.put("languageCode", "pt-BR");
                voice.put("name", "pt-BR-Standard-B"); // voz masculina; troque por A para feminina
                body.set("voice", voice);

                // Formato de saída: MP3
                ObjectNode audioConfig = mapper.createObjectNode();
                audioConfig.put("audioEncoding", "MP3");
                audioConfig.put("speakingRate", 1.0); // 0.25 a 4.0 (1.0 = normal)
                audioConfig.put("pitch", 0.0); // -20.0 a 20.0 semitones
                body.set("audioConfig", audioConfig);

                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(ttsEndpoint))
                                .header("Content-Type", "application/json")
                                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                                .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                System.out.println("TTS HTTP Status: " + response.statusCode());

                if (response.statusCode() != 200) {
                        fail("Falha na API TTS. Status: " + response.statusCode() + "\nBody: " + response.body());
                }

                JsonNode responseJson = mapper.readTree(response.body());
                String audioContentBase64 = responseJson.get("audioContent").asText();

                return Base64.getDecoder().decode(audioContentBase64);
        }
}