package com.purchase.rewards.receipt.processor.model.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

/**
 * Payload for incoming receipt to be processed
 */
public class ReceiptDTO {
    
    @NotBlank(message = "Retailer name must be provided")
    private String retailer;
    @NotNull(message = "Purchase date must be provided")
    private LocalDate purchaseDate;
    @NotNull(message = "Purchase time must be provided")
    private LocalTime purchaseTime;
    @NotNull(message = "Receipt total must be provided")
    private Float total;
    
    @Valid
    @NotEmpty(message = "Purchased items must be provided")
    private List<ItemsDTO> items;

    @Override
    public String toString() {
        return "ReceiptDTO [retailer=" + retailer + ", purchaseDate=" + purchaseDate + ", purchaseTime=" + purchaseTime
                + ", total=" + total + ", items=" + items + "]";
    }
    
    
    public String getRetailer() {
        return retailer;
    }
    public void setRetailer(String retalier) {
        this.retailer = retalier;
    }
    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }
    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
    public LocalTime getPurchaseTime() {
        return purchaseTime;
    }
    public void setPurchaseTime(LocalTime purchaseTime) {
        this.purchaseTime = purchaseTime;
    }
    public float getTotal() {
        return total;
    }
    public void setTotal(float total) {
        this.total = total;
    }
    public List<ItemsDTO> getItems() {
        return items;
    }
    public void setItems(List<ItemsDTO> items) {
        this.items = items;
    }
}
