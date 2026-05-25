package br.com.maiawall.finances.domain.entity;

import java.math.BigDecimal;

import br.com.maiawall.finances.domain.enums.Category;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transaction {

    private TransactionId id;
    private String description;
    private BigDecimal amount;
    private Category category;

    public Transaction(String description, BigDecimal amount, Category category) {
        this.id = new TransactionId();
        this.description = description;
        this.amount = amount;
        this.category = category;
    }

}
