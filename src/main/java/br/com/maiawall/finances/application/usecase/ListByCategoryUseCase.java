package br.com.maiawall.finances.application.usecase;

import java.util.List;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import br.com.maiawall.finances.application.usecase.output.TransactionPersistOutput;
import br.com.maiawall.finances.domain.enums.Category;
import br.com.maiawall.finances.domain.repository.TransactionRepository;

@Service
public class ListByCategoryUseCase {

    private final TransactionRepository transactionRepository;

    public ListByCategoryUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "ListByCategory", description = "Use case para listar as transações financeiras por categoria.")
    public List<TransactionPersistOutput> execute(
            @ToolParam(description = "A categoria das transações a serem listadas.") Category category) {
        var transactions = transactionRepository.findAllByCategory(category);
        return transactions.stream()
                .map(TransactionPersistOutput::from)
                .toList();
    }

}
