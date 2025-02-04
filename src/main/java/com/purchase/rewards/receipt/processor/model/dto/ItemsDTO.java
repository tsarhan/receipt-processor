package com.purchase.rewards.receipt.processor.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ItemsDTO {
    
    @NotBlank(message = "Purchased item description must be provided")
    private String shortDescription;
    @NotNull
    private Float price;
    
    public String getShortDescription() {
        return shortDescription;
    }
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }
    public float getPrice() {
        return price;
    }
    public void setPrice(float price) {
        this.price = price;
    }

}
