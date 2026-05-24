package br.com.maiawall.finances.infra.http.response;

import java.math.BigDecimal;

import br.com.maiawall.finances.application.usecase.output.TransactionPersistOutput;

public record TransactionResponseDTO(String id, String description, BigDecimal amount, String category) {

    public static TransactionResponseDTO from(TransactionPersistOutput transaction) {
        return new TransactionResponseDTO(transaction.id(), transaction.description(),
                transaction.amount(), transaction.category());
    }

}
