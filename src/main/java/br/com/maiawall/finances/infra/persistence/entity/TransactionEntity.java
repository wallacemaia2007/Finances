package br.com.maiawall.finances.infra.persistence.entity;

import java.math.BigDecimal;
import java.util.UUID;

import br.com.maiawall.finances.domain.enums.Category;
import br.com.maiawall.finances.domain.entity.Transaction;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntity {

    @Id
    private UUID id;
    private String description;
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Category category;

    public static TransactionEntity from(Transaction transaction) {
        TransactionEntity entity = new TransactionEntity(transaction.getId().id(), transaction.getDescription(),
                transaction.getAmount(), transaction.getCategory());
        return entity;
    }

    public Transaction toDomain() {
        return new Transaction(this.description, this.amount, this.category);
    }

}
