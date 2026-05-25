package br.com.maiawall.finances.application.usecase;

import org.springframework.stereotype.Service;

import br.com.maiawall.finances.application.usecase.input.TransactionPersistInput;
import br.com.maiawall.finances.application.usecase.output.TransactionPersistOutput;
import br.com.maiawall.finances.domain.entity.Transaction;
import br.com.maiawall.finances.domain.repository.TransactionRepository;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

@Service
public class TransactionPersistUseCase {

    private final TransactionRepository transactionRepository;

    public TransactionPersistUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Tool(name = "TransactionPersist", description = "Use case para persistir uma transação financeira.")
    public TransactionPersistOutput execute(
            @ToolParam(description = "Os dados da transação a ser persistida.") TransactionPersistInput input) {
        Transaction transaction = new Transaction(input.description(), input.amount(), input.category());
        var savedTransaction = transactionRepository.save(transaction);
        return TransactionPersistOutput.from(savedTransaction);
    }
}
