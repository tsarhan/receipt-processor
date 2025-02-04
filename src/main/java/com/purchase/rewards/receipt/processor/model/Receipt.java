package com.purchase.rewards.receipt.processor.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

/**
 * Domain class for persisting processed Receipt into database
 */
@Entity
public class Receipt {
    
    @Id
    @UuidGenerator
    private UUID id;
    private String retailer;
    private LocalDate purchaseDate;
    private LocalTime purchaseTime;
    private float total;
    private int points;

    @OneToMany(mappedBy = "receipt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Item> items;

    public int getPoints() {
        return points;
    }
    public void setPoints(int points) {
        this.points = points;
    }
    
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public String getRetailer() {
        return retailer;
    }
    public void setRetailer(String retailer) {
        this.retailer = retailer;
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
    public List<Item> getItems() {
        return items;
    }
    public void setItems(List<Item> items) {
        this.items = items;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((retailer == null) ? 0 : retailer.hashCode());
        result = prime * result + ((purchaseDate == null) ? 0 : purchaseDate.hashCode());
        result = prime * result + ((purchaseTime == null) ? 0 : purchaseTime.hashCode());
        result = prime * result + Float.floatToIntBits(total);
        result = prime * result + points;
        result = prime * result + ((items == null) ? 0 : items.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Receipt other = (Receipt) obj;
        if (retailer == null) {
            if (other.retailer != null)
                return false;
        } else if (!retailer.equals(other.retailer))
            return false;
        if (purchaseDate == null) {
            if (other.purchaseDate != null)
                return false;
        } else if (!purchaseDate.equals(other.purchaseDate))
            return false;
        if (purchaseTime == null) {
            if (other.purchaseTime != null)
                return false;
        } else if (!purchaseTime.equals(other.purchaseTime))
            return false;
        if (Float.floatToIntBits(total) != Float.floatToIntBits(other.total))
            return false;
        if (points != other.points)
            return false;
        if (items == null) {
            if (other.items != null)
                return false;
        } else if (!items.equals(other.items))
            return false;
        return true;
    }

}
