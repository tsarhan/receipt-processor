package com.purchase.rewards.receipt.processor.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.purchase.rewards.receipt.processor.model.dto.ReceiptCreatedDTO;
import com.purchase.rewards.receipt.processor.model.dto.ReceiptDTO;
import com.purchase.rewards.receipt.processor.model.dto.ReceiptPointsDTO;
import com.purchase.rewards.receipt.processor.service.ReceiptProcessorService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/receipts")
public class ReceiptProcessorController {

    private ReceiptProcessorService receiptService;

    @Autowired
    public ReceiptProcessorController(ReceiptProcessorService receiptService) {
        this.receiptService = receiptService;
    }

    @PostMapping(path = "/process")
    public ResponseEntity<ReceiptCreatedDTO> processReceipt(@RequestBody @Valid ReceiptDTO receiptDTO) {
        UUID receiptId = receiptService.processReceipt(receiptDTO);
        ReceiptCreatedDTO receiptCreatedDTO = new ReceiptCreatedDTO();
        receiptCreatedDTO.setId(receiptId);
        return new ResponseEntity<ReceiptCreatedDTO>(receiptCreatedDTO , HttpStatus.OK);
    }

    @GetMapping(path = "/{id}/points")
    public ResponseEntity<ReceiptPointsDTO> getReceiptPoints(@PathVariable String id) {
        UUID uuid = parseUUID(id);
        int points = receiptService.getReceiptPoints(uuid);
        ReceiptPointsDTO pointsDTO = new ReceiptPointsDTO();
        pointsDTO.setPoints(points);
        return new ResponseEntity<ReceiptPointsDTO>(pointsDTO , HttpStatus.OK);
    }

    private UUID parseUUID(String id) {
        UUID uuid = null;
        try {
            uuid = UUID.fromString(id);
        }
        catch(IllegalArgumentException e) {
            throw new EntityNotFoundException(ReceiptProcessorService.RECEIPT_NOT_FOUND_MSG);
        }
        return uuid;
    }

}
