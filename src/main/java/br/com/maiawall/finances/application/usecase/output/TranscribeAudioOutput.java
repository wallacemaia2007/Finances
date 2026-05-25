package br.com.maiawall.finances.application.usecase.output;

public record TranscribeAudioOutput(String text) {
    public static TranscribeAudioOutput from(String text) {
        return new TranscribeAudioOutput(text);
    }
}
