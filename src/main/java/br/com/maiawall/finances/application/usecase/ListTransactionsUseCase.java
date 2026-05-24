package br.com.maiawall.finances.application.usecase;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.maiawall.finances.application.usecase.output.TransactionPersistOutput;
import br.com.maiawall.finances.domain.TransactionRepository;

@Service
public class ListTransactionsUseCase {

    private final TransactionRepository transactionRepository;

    public ListTransactionsUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<TransactionPersistOutput> execute() {
        var transactions = transactionRepository.findAll();
        return transactions.stream()
                .map(TransactionPersistOutput::from)
                .toList();
    }

}
