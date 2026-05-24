package br.com.maiawall.finances.infra.persistence.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import br.com.maiawall.finances.domain.Category;
import br.com.maiawall.finances.domain.Transaction;
import br.com.maiawall.finances.domain.TransactionRepository;
import br.com.maiawall.finances.infra.persistence.entity.TransactionEntity;

@Repository
public class JpaTransactionRepository implements TransactionRepository {

    private final TransactionEntityRepository transactionEntityRepository;

    public JpaTransactionRepository(TransactionEntityRepository transactionEntityRepository) {
        this.transactionEntityRepository = transactionEntityRepository;
    }

    @Override
    public Transaction save(Transaction transaction) {
        var entity = TransactionEntity.from(transaction);
        var savedEntity = transactionEntityRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public List<Transaction> findAllByCategory(Category category) {
        var entities = transactionEntityRepository.findAllByCategory(category);
        return entities.stream().map(TransactionEntity::toDomain).toList();
    }

}
