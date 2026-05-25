package br.com.maiawall.finances.infra.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.maiawall.finances.domain.enums.Category;
import br.com.maiawall.finances.infra.persistence.entity.TransactionEntity;

@Repository
public interface TransactionEntityRepository extends JpaRepository<TransactionEntity, UUID> {

    List<TransactionEntity> findAllByCategory(Category category);

}
