package br.com.maiawall.finances.application.usecase.input;

import org.springframework.web.multipart.MultipartFile;

public record TranscribeAudioInput(MultipartFile file) {
}
