package br.com.maiawall.finances.infra.http.request;

import java.math.BigDecimal;

import br.com.maiawall.finances.application.usecase.input.TransactionPersistInput;
import br.com.maiawall.finances.domain.enums.Category;

public record TransactionRequestDTO(String description, Category category, BigDecimal amount) {

    public TransactionPersistInput toInput() {
        return new TransactionPersistInput(description, amount, category);
    }

}
