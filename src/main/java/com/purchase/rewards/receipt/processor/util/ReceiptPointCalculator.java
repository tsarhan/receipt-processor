package com.purchase.rewards.receipt.processor.util;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.purchase.rewards.receipt.processor.controller.GlobalControllerAdvice;
import com.purchase.rewards.receipt.processor.model.dto.ItemsDTO;
import com.purchase.rewards.receipt.processor.model.dto.ReceiptDTO;

@Component
public class ReceiptPointCalculator {

    private Logger logger = LoggerFactory.getLogger(ReceiptPointCalculator.class);

    @Value("${odd.day.points:6}")
    private int oddDayPoints;

    @Value("${purchase.time.range.points:10}")
    private int timePoints;

    @Value("${item.pair.points:5}")
    private int itemPairPoints;

    @Value("${item.description.length.multiple.of:3}")
    private int itemDescriptionLengthMutiple;

    @Value("${item.price.multiplier:.2}")
    private float itemPriceMultipler;

    @Value("${total.purchase.multiple.of:.25}")
    private float totalPurchaseMultipleOf;

    @Value("${purchase.time.range.start}")
    private LocalTime purchaseTimeRangeStart;

    @Value("${purchase.time.range.end}")
    private LocalTime purchaseTimeRangeEnd;

    @Value("${total.no.cents.points:50}")
    private int totalNoCentsPoints;

    @Value("${total.purchase.multiple.of.points:25}")
    private int totalPurchaseMultipleOfPoints;


    public int calculatePoints(ReceiptDTO receiptDTO) {
        if(Objects.isNull(receiptDTO)) throw new IllegalArgumentException(GlobalControllerAdvice.RECEIPT_IS_INVALID);

        int points = 0;
        points += calculateRetailerNamePoints(receiptDTO.getRetailer());
        points += calculateTotalPoints(receiptDTO.getTotal());
        points += calculateItemPoints(receiptDTO.getItems());
        points += calculateDateTimePoints(receiptDTO.getPurchaseDate(), receiptDTO.getPurchaseTime());

        return points;
    }

    private int calculateDateTimePoints(LocalDate purchaseDate, LocalTime purchaseTime) {
        if(Objects.isNull(purchaseDate) || Objects.isNull(purchaseTime)) throw new IllegalArgumentException(GlobalControllerAdvice.RECEIPT_IS_INVALID);
        int points = 0;
        
        //Odd days get extra points
        if(purchaseDate.getDayOfMonth() <= 2 || purchaseDate.getDayOfMonth() % 2 != 0) {
            points+=oddDayPoints;
            logger.debug("Odd Date points={}", points);
        }

        if(purchaseTime.compareTo(purchaseTimeRangeStart) > 0 && purchaseTime.compareTo(purchaseTimeRangeEnd) < 0) {
            points+=timePoints;
            logger.debug("Time Range points={}", points);
        }

        return points;

    }

    private int calculateItemPoints(List<ItemsDTO> items) {
        if(CollectionUtils.isEmpty(items)) throw new IllegalArgumentException(GlobalControllerAdvice.RECEIPT_IS_INVALID);
        //every two items get extra points
        int pointsByCount = (items.size()/2) * itemPairPoints;
        int pointsByCharacterCount = 0;
        
        for(ItemsDTO item : items) {
            if(item.getShortDescription().trim().length() % itemDescriptionLengthMutiple == 0) {
                pointsByCharacterCount+= Math.ceil(item.getPrice() * itemPriceMultipler);
            }
        }

        logger.debug("Items pair points={}", pointsByCount);
        logger.debug("Item pointsByCharacterCount={}", pointsByCharacterCount);

        return pointsByCount+pointsByCharacterCount;
    }

    private int calculateTotalPoints(float total) {
        int points = 0;
        
        //No decimal points
        if (total % 1 == 0) {
            points += totalNoCentsPoints;
            logger.debug("Total purchase decimal points={}", points);
        }

        //Total Multiple of value below
        if (total % totalPurchaseMultipleOf == 0) {
            points += totalPurchaseMultipleOfPoints;
            logger.debug("Total purchase multiple {}"+" points= {}", totalPurchaseMultipleOf, points);
        }

        logger.debug("Total purchase points={}", points);
        return points;
    }

    private int calculateRetailerNamePoints(String retailer) {
        if(Objects.isNull(retailer)) throw new IllegalArgumentException(GlobalControllerAdvice.RECEIPT_IS_INVALID);
        
        int points = 0;

        for (int i = 0; i < retailer.length(); ++i) {
            final int codePoint = retailer.codePointAt(i);
            if (Character.isLetterOrDigit(codePoint)) {
                points++;
            }
        }

        logger.debug("Retailer name points={}", points);

        return points;
    }
}
