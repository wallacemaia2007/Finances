package br.com.maiawall.finances.application.usecase.input;

import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.web.multipart.MultipartFile;

public record TranscribeAudioInput(
        @ToolParam(description = "O arquivo de áudio a ser transcrito.") MultipartFile file) {
}
