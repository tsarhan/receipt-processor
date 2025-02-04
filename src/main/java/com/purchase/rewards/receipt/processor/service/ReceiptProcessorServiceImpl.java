package com.purchase.rewards.receipt.processor.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import com.purchase.rewards.receipt.processor.model.Item;
import com.purchase.rewards.receipt.processor.model.Receipt;
import com.purchase.rewards.receipt.processor.model.dto.ItemsDTO;
import com.purchase.rewards.receipt.processor.model.dto.ReceiptDTO;
import com.purchase.rewards.receipt.processor.repository.ReceiptRepository;
import com.purchase.rewards.receipt.processor.util.ReceiptPointCalculator;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ReceiptProcessorServiceImpl implements ReceiptProcessorService {
    private Logger logger = LoggerFactory.getLogger(ReceiptProcessorServiceImpl.class);

    private ReceiptRepository receiptRepository;

    private ReceiptPointCalculator pointCalculator;

    public ReceiptProcessorServiceImpl() {
    }

    @Autowired
    public ReceiptProcessorServiceImpl(ReceiptRepository receiptRepository,
            ReceiptPointCalculator receiptPointCalculator) {
        this.receiptRepository = receiptRepository;
        this.pointCalculator = receiptPointCalculator;
    }

    @Override
    public UUID processReceipt(ReceiptDTO receiptDTO) {
        Receipt receipt = createDomainReceipt(receiptDTO);

        validateNewReceipt(receipt);

        Receipt saved = receiptRepository.save(receipt);

        logger.debug("Processed and saved receipt");

        return saved.getId();
    }

    @Override
    public int getReceiptPoints(UUID receiptId) {
        if (!receiptRepository.existsById(receiptId))
            throw new EntityNotFoundException(RECEIPT_NOT_FOUND_MSG);

        logger.debug("Returning points for receipt");

        return receiptRepository.getPointsById(receiptId);
    }

    private List<Item> createDomainItems(Receipt receipt, ReceiptDTO receiptDTO) {
        List<Item> domainItems = new ArrayList<>();

        for (ItemsDTO item : receiptDTO.getItems()) {
            Item domainItem = new Item();
            domainItem.setPrice(item.getPrice());
            domainItem.setShortDescription(item.getShortDescription());
            domainItem.setReceipt(receipt);
            domainItems.add(domainItem);

        }

        return domainItems;
    }

    /**
     * Validates receipt is new but does not do a deep check.
     * @param receipt to validate is new
     */
    private void validateNewReceipt(Receipt receipt) {
        Example<Receipt> example = Example.of(receipt);
        if (receiptRepository.exists(example))
            throw new EntityExistsException(RECEIPT_ALREADY_EXISTS);
    }

    private Receipt createDomainReceipt(ReceiptDTO receiptDTO) {
        Receipt receipt = new Receipt();
        receipt.setPurchaseDate(receiptDTO.getPurchaseDate());
        receipt.setPurchaseTime(receiptDTO.getPurchaseTime());
        receipt.setRetailer(receiptDTO.getRetailer().trim());
        receipt.setTotal(receiptDTO.getTotal());
        receipt.setItems(createDomainItems(receipt, receiptDTO));
        receipt.setPoints(pointCalculator.calculatePoints(receiptDTO));

        return receipt;
    }

}
