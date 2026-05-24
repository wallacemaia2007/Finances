package br.com.maiawall.finances.application.usecase.input;

import java.math.BigDecimal;

import br.com.maiawall.finances.domain.Category;

public record TransactionPersistInput(String description, BigDecimal amount, Category category) {

}
