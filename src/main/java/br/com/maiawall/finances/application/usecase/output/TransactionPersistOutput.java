package br.com.maiawall.finances.application.usecase.output;

import java.math.BigDecimal;

import br.com.maiawall.finances.domain.Transaction;

public record TransactionPersistOutput(String id, String description, BigDecimal amount, String category) {

    public static TransactionPersistOutput from(Transaction transaction) {
        return new TransactionPersistOutput(
                transaction.getId().id().toString(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getCategory().name());
    }

}
