package br.com.maiawall.finances.infra.http.audio;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import br.com.maiawall.finances.application.usecase.audio.TranscribeAudioUseCase;
import br.com.maiawall.finances.application.usecase.input.TranscribeAudioInput;

@RestController
@RequestMapping("/api")
public class TranscriptionController {

        private final TranscribeAudioUseCase transcribeAudioUseCase;

        public TranscriptionController(TranscribeAudioUseCase transcribeAudioUseCase) {
                this.transcribeAudioUseCase = transcribeAudioUseCase;
        }

        @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public String transcribe(@RequestParam("file") MultipartFile file) throws Exception {
                var output = transcribeAudioUseCase.execute(new TranscribeAudioInput(file));
                return output.text();
        }
}