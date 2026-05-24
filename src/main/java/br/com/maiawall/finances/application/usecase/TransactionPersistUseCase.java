package br.com.maiawall.finances.application.usecase;

import org.springframework.stereotype.Service;

import br.com.maiawall.finances.application.usecase.input.TransactionPersistInput;
import br.com.maiawall.finances.application.usecase.output.TransactionPersistOutput;
import br.com.maiawall.finances.domain.Transaction;
import br.com.maiawall.finances.domain.TransactionRepository;

@Service
public class TransactionPersistUseCase {

    private final TransactionRepository transactionRepository;

    public TransactionPersistUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public TransactionPersistOutput execute(TransactionPersistInput input) {
        Transaction transaction = new Transaction(input.description(), input.amount(), input.category());
        var savedTransaction = transactionRepository.save(transaction);
        return TransactionPersistOutput.from(savedTransaction);
    }
}
