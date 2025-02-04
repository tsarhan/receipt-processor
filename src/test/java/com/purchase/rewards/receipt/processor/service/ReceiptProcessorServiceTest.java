package com.purchase.rewards.receipt.processor.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.IOException;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Example;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.purchase.rewards.receipt.processor.model.Receipt;
import com.purchase.rewards.receipt.processor.model.dto.ReceiptDTO;
import com.purchase.rewards.receipt.processor.repository.ReceiptRepository;
import com.purchase.rewards.receipt.processor.util.ReceiptPointCalculator;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class)
public class ReceiptProcessorServiceTest {

    @Mock
    ReceiptRepository receiptRepository;

    @Mock
    ReceiptPointCalculator receiptPointCalculator;

    private ReceiptProcessorService receiptService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void initService() {
        receiptService = new ReceiptProcessorServiceImpl(receiptRepository, receiptPointCalculator);
        // MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void receiveReceiptDTO_processReceipt_persistInDB() throws StreamReadException, DatabindException, IOException {
        ReceiptDTO receiptDTO = loadReceiptDTO("examples/target.json");

        ModelMapper localModelMapper = new ModelMapper();
        Receipt receipt = localModelMapper.map(receiptDTO, Receipt.class);

        given(receiptPointCalculator.calculatePoints(any(ReceiptDTO.class))).willReturn(28);
        receipt.setId(UUID.randomUUID());
        given(receiptRepository.exists(any(Example.class))).willReturn(false);
        given(receiptRepository.save(any(Receipt.class))).willReturn(receipt);

        UUID receiptId = receiptService.processReceipt(receiptDTO);

        assertEquals(receipt.getId(), receiptId);

        verify(receiptPointCalculator).calculatePoints(receiptDTO);
        verify(receiptRepository).exists(any(Example.class));
        verify(receiptRepository).save(any(Receipt.class));
    }

    @Test
    public void receiveReceiptDTO_receiptNotNew_throwException() throws StreamReadException, DatabindException, IOException {
        ReceiptDTO receiptDTO = loadReceiptDTO("examples/target.json");

        ModelMapper localModelMapper = new ModelMapper();
        Receipt receipt = localModelMapper.map(receiptDTO, Receipt.class);

        given(receiptPointCalculator.calculatePoints(any(ReceiptDTO.class))).willReturn(28);
        receipt.setId(UUID.randomUUID());
        given(receiptRepository.exists(any(Example.class))).willReturn(true);

        Exception exception = assertThrows(EntityExistsException.class, () -> receiptService.processReceipt(receiptDTO));
        assertEquals(exception.getMessage(), ReceiptProcessorService.RECEIPT_ALREADY_EXISTS);

        verify(receiptPointCalculator).calculatePoints(receiptDTO);
        verify(receiptRepository).exists(any(Example.class));
        verifyNoMoreInteractions(receiptRepository);
    }

    @Test
    public void receiveReceiptId_receiptExists_getPoints() throws StreamReadException, DatabindException, IOException {
        UUID receiptId = UUID.randomUUID();

        given(receiptRepository.existsById(receiptId)).willReturn(true);
        given(receiptRepository.getPointsById(receiptId)).willReturn(28);

        int points = receiptService.getReceiptPoints(receiptId);

        assertEquals(points, 28);
        verify(receiptRepository).getPointsById(receiptId);
        verify(receiptRepository).existsById(receiptId);
    }

    @Test
    public void receiveReceiptId_receiptDoeNotExist_throwException() throws StreamReadException, DatabindException, IOException {
        UUID receiptId = UUID.randomUUID();

        given(receiptRepository.existsById(receiptId)).willReturn(false);

        Exception exception = assertThrows(EntityNotFoundException.class, () -> receiptService.getReceiptPoints(receiptId));
        
        assertEquals(exception.getMessage(), ReceiptProcessorService.RECEIPT_NOT_FOUND_MSG);

        verify(receiptRepository).existsById(receiptId);
        verifyNoMoreInteractions(receiptRepository);
    }

    private ReceiptDTO loadReceiptDTO(String path) throws StreamReadException, DatabindException, IOException {
        ClassPathResource resource = new ClassPathResource(path);
        
        return objectMapper.readValue(resource.getContentAsByteArray(), ReceiptDTO.class);
    }

}
