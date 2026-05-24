package br.com.maiawall.finances.domain;

import java.util.UUID;

public record TransactionId(UUID id) {

    public TransactionId() {
        this(UUID.randomUUID());
    }

}
