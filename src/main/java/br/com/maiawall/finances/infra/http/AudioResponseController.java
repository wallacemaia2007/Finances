package br.com.maiawall.finances.infra.http;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.maiawall.finances.application.usecase.audio.SynthesizeAudioUseCase;
import br.com.maiawall.finances.infra.http.request.SynthesizeAudioRequest;

@RestController
@RequestMapping("/api")
public class AudioResponseController {

        private final SynthesizeAudioUseCase synthesizeAudioUseCase;

        public AudioResponseController(SynthesizeAudioUseCase synthesizeAudioUseCase) {
                this.synthesizeAudioUseCase = synthesizeAudioUseCase;
        }

        @PostMapping(value = "/chat", consumes = MediaType.APPLICATION_JSON_VALUE, produces = "audio/mpeg")
        public ResponseEntity<ByteArrayResource> sinthesize(@RequestBody SynthesizeAudioRequest request)
                        throws Exception {

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

                var output = synthesizeAudioUseCase.execute(request.toInput());

                return ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_DISPOSITION,
                                                ContentDisposition.attachment().filename("audio.mp3").build()
                                                                .toString())
                                .body(new ByteArrayResource(output.audio()));
        }
}