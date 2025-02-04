package com.purchase.rewards.receipt.processor.model.dto;

import java.util.UUID;

/**
 * Payload of processed and persisted receipt id
 */
public class ReceiptCreatedDTO {
    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
