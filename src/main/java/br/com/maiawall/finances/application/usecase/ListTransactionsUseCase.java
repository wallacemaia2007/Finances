package br.com.maiawall.finances.application.usecase;

import java.util.List;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import br.com.maiawall.finances.application.usecase.output.TransactionPersistOutput;
import br.com.maiawall.finances.domain.repository.TransactionRepository;

@Service
public class ListTransactionsUseCase {

    private final TransactionRepository transactionRepository;

    public ListTransactionsUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "ListTransactions", description = "Use case para listar todas as transações financeiras.")
    public List<TransactionPersistOutput> execute() {
        var transactions = transactionRepository.findAll();
        return transactions.stream()
                .map(TransactionPersistOutput::from)
                .toList();
    }

}
