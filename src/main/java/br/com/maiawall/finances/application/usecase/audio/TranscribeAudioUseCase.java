package br.com.maiawall.finances.application.usecase.audio;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.content.Media;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import br.com.maiawall.finances.application.usecase.input.TranscribeAudioInput;
import br.com.maiawall.finances.application.usecase.output.TranscribeAudioOutput;

@Service
public class TranscribeAudioUseCase {

    private final ChatClient chatClient;

    public TranscribeAudioUseCase(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public TranscribeAudioOutput execute(TranscribeAudioInput input) throws Exception {
        var file = input.file();

        Media audioMedia = new Media(
                MimeTypeUtils.parseMimeType(file.getContentType()),
                file.getResource());

        UserMessage userMessage = UserMessage.builder()
                .text("""
                        Transcreva o áudio para português.
                        Retorne apenas a transcrição.
                        """)
                .media(audioMedia)
                .build();

        String response = chatClient.prompt()
                .messages(userMessage)
                .call()
                .content();

        return TranscribeAudioOutput.from(response);
    }
}
