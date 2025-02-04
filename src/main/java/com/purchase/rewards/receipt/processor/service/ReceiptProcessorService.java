package com.purchase.rewards.receipt.processor.service;

import java.util.UUID;

import com.purchase.rewards.receipt.processor.model.dto.ReceiptDTO;

/**
 * Service with methods for processing receipts
 */
public interface ReceiptProcessorService {
    public final static String RECEIPT_NOT_FOUND_MSG = "No receipt found for that ID.";
    public final static String RECEIPT_ALREADY_EXISTS = "The receipt already exists.";
    
    /**
     * Processes receipts for determining points and storing in database
     * 
     * @param receipt
     * @return generated Id of receipt after saving in database
     */
    public UUID processReceipt(ReceiptDTO receipt);

    /**
     * Returns points for a processed receipt based on id if it exists.
     * 
     * @param receiptId
     * @return points earned by processed receipt
     */
    public int getReceiptPoints(UUID receiptId);
}