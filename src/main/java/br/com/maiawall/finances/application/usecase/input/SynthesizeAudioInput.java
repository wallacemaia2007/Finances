package br.com.maiawall.finances.application.usecase.input;

import org.springframework.ai.tool.annotation.ToolParam;

public record SynthesizeAudioInput(@ToolParam(description = "O texto a ser sintetizado em áudio.") String text) {
}
