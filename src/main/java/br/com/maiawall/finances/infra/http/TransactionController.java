package br.com.maiawall.finances.infra.http;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.maiawall.finances.application.usecase.ListByCategoryUseCase;
import br.com.maiawall.finances.application.usecase.ListTransactionsUseCase;
import br.com.maiawall.finances.application.usecase.TransactionPersistUseCase;
import br.com.maiawall.finances.domain.Category;
import br.com.maiawall.finances.infra.http.request.TransactionRequestDTO;
import br.com.maiawall.finances.infra.http.response.TransactionResponseDTO;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionPersistUseCase transactionPersistUseCase;
    private final ListTransactionsUseCase listTransactionsUseCase;
    private final ListByCategoryUseCase listTransactionsByCategoryUseCase;

    public TransactionController(TransactionPersistUseCase transactionPersistUseCase,
            ListTransactionsUseCase listTransactionsUseCase, ListByCategoryUseCase listTransactionsByCategoryUseCase) {
        this.transactionPersistUseCase = transactionPersistUseCase;
        this.listTransactionsUseCase = listTransactionsUseCase;
        this.listTransactionsByCategoryUseCase = listTransactionsByCategoryUseCase;
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

}
