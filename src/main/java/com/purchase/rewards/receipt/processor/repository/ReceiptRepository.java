package com.purchase.rewards.receipt.processor.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.purchase.rewards.receipt.processor.model.Receipt;

public interface ReceiptRepository extends JpaRepository<Receipt, UUID>{
    @Query("select points from Receipt where id =:receiptId")
    public int getPointsById(UUID receiptId);
}
