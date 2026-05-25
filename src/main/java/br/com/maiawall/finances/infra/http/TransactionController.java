package br.com.maiawall.finances.infra.http;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.lang3.CharSet;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.beans.factory.annotation.Value;

import br.com.maiawall.finances.application.usecase.ListByCategoryUseCase;
import br.com.maiawall.finances.application.usecase.ListTransactionsUseCase;
import br.com.maiawall.finances.application.usecase.TransactionPersistUseCase;
import br.com.maiawall.finances.application.usecase.audio.SynthesizeAudioUseCase;
import br.com.maiawall.finances.application.usecase.audio.TranscribeAudioUseCase;
import br.com.maiawall.finances.application.usecase.input.TranscribeAudioInput;
import br.com.maiawall.finances.domain.enums.Category;
import br.com.maiawall.finances.infra.http.request.TransactionRequestDTO;
import br.com.maiawall.finances.infra.http.response.TransactionResponseDTO;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionPersistUseCase transactionPersistUseCase;
    private final ListTransactionsUseCase listTransactionsUseCase;
    private final ListByCategoryUseCase listTransactionsByCategoryUseCase;
    private final TranscribeAudioUseCase transcribeAudioUseCase;
    private final SynthesizeAudioUseCase synthesizeAudioUseCase;
    private final ChatClient chatClient;

    public TransactionController(TransactionPersistUseCase transactionPersistUseCase,
            ListTransactionsUseCase listTransactionsUseCase, ListByCategoryUseCase listTransactionsByCategoryUseCase,
            TranscribeAudioUseCase transcribeAudioUseCase, SynthesizeAudioUseCase synthesizeAudioUseCase,
            ChatClient.Builder chatClient,
            @Value("classpath:/prompts/system.st") Resource systemPrompt) throws IOException {

        this.transactionPersistUseCase = transactionPersistUseCase;
        this.listTransactionsUseCase = listTransactionsUseCase;
        this.listTransactionsByCategoryUseCase = listTransactionsByCategoryUseCase;
        this.transcribeAudioUseCase = transcribeAudioUseCase;
        this.synthesizeAudioUseCase = synthesizeAudioUseCase;
        this.chatClient = chatClient
                .defaultTools(transactionPersistUseCase, listTransactionsUseCase, listTransactionsByCategoryUseCase,
                        transcribeAudioUseCase, synthesizeAudioUseCase)
                .defaultSystem(systemPrompt.getContentAsString(Charset.defaultCharset()))
                .build();
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(@RequestBody TransactionRequestDTO request) {
        var transaction = transactionPersistUseCase.execute(request.toInput());
        return ResponseEntity.status(HttpStatus.CREATED).body(TransactionResponseDTO.from(transaction));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> listTransactions() {
        var transactions = listTransactionsUseCase.execute();
        var transactionsResponse = transactions.stream()
                .map(TransactionResponseDTO::from)
                .toList();
        return ResponseEntity.ok(transactionsResponse);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<TransactionResponseDTO>> listTransactionsByCategory(@PathVariable String category) {
        var transactions = listTransactionsByCategoryUseCase.execute(Category.valueOf(category));
        var transactionsResponse = transactions.stream()
                .map(TransactionResponseDTO::from)
                .toList();
        return ResponseEntity.ok(transactionsResponse);
    }

    @PostMapping(value = "/ai", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String transcribe(@RequestParam("file") MultipartFile file) throws Exception {
        var output = transcribeAudioUseCase.execute(new TranscribeAudioInput(file));

        var result = chatClient.prompt()
                .user(output.text())
                .call()
                .content();

        return result;
    }

}
