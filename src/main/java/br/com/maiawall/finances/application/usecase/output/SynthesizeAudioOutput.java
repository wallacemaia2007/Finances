package br.com.maiawall.finances.application.usecase.output;

public record SynthesizeAudioOutput(byte[] audio) {

    public static SynthesizeAudioOutput from(byte[] audio) {
        return new SynthesizeAudioOutput(audio);
    }
}
