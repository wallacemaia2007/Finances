package br.com.maiawall.finances.infra.http.request;

import br.com.maiawall.finances.application.usecase.input.SynthesizeAudioInput;

public record SynthesizeAudioRequest(String text) {

    public SynthesizeAudioInput toInput() {
        return new SynthesizeAudioInput(text);
    }

}
