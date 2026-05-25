package br.com.maiawall.finances.application.usecase.input;

import java.math.BigDecimal;

import org.springframework.ai.tool.annotation.ToolParam;

import br.com.maiawall.finances.domain.Category;

public record TransactionPersistInput(
        @ToolParam(description = "A descrição da transação.") String description,
        @ToolParam(description = "O valor da transação.") BigDecimal amount,
        @ToolParam(description = "A categoria da transação.") Category category) {

}
