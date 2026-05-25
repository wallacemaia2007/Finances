package br.com.maiawall.finances.domain.repository;

import java.util.List;

import br.com.maiawall.finances.domain.enums.Category;
import br.com.maiawall.finances.domain.entity.Transaction;

public interface TransactionRepository {

    Transaction save(Transaction transaction);

    List<Transaction> findAllByCategory(Category category);

    List<Transaction> findAll();

}
