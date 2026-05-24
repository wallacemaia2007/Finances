package br.com.maiawall.finances.application.usecase;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.maiawall.finances.application.usecase.output.TransactionPersistOutput;
import br.com.maiawall.finances.domain.Category;
import br.com.maiawall.finances.domain.TransactionRepository;

@Service
public class ListByCategoryUseCase {

    private final TransactionRepository transactionRepository;

    public ListByCategoryUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<TransactionPersistOutput> execute(Category category) {
        var transactions = transactionRepository.findAllByCategory(category);
        return transactions.stream()
                .map(TransactionPersistOutput::from)
                .toList();
    }

}
