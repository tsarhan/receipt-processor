package com.purchase.rewards.receipt.processor.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import com.purchase.rewards.receipt.processor.model.dto.ReceiptDTO;
import com.purchase.rewards.receipt.processor.model.dto.ReceiptPointsDTO;
import com.purchase.rewards.receipt.processor.service.ReceiptProcessorService;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ReceiptProcessorController.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class ReceiptProcessorControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    ReceiptProcessorService receiptService;

    @Test
    public void givenReceipt_ProcessReceipt_thenReturnReceiptId()
            throws Exception {

        UUID id = UUID.randomUUID();
        ClassPathResource resource = new ClassPathResource("examples/target.json");

        given(receiptService.processReceipt(any(ReceiptDTO.class))).willReturn(id);

        mvc.perform(post("/receipts/process").content(resource.getContentAsByteArray())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", Is.is(id.toString())));
    }

    @Test
    public void givenReceiptId_returnPoints()
            throws Exception {
        int points = 28;
        UUID id = UUID.randomUUID();
        ReceiptPointsDTO receiptPointsDTO = new ReceiptPointsDTO();
        receiptPointsDTO.setPoints(points);

        given(receiptService.getReceiptPoints(id)).willReturn(points);

        mvc.perform(get("/receipts/"+id+"/points")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points", Is.is(points)));
    }

    @Test
    public void givenReceiptMissingRetailer_validateReceipt_throwException()
            throws Exception {

        ClassPathResource resource = new ClassPathResource("examples/missing-retailer.json");

        mvc.perform(post("/receipts/process").content(resource.getContentAsByteArray())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(GlobalControllerAdvice.RECEIPT_IS_INVALID));
    }

    @Test
    public void givenReceiptMissingPurchaseDate_validateReceipt_throwException()
            throws Exception {

        ClassPathResource resource = new ClassPathResource("examples/missing-purchase-date.json");

        mvc.perform(post("/receipts/process").content(resource.getContentAsByteArray())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(GlobalControllerAdvice.RECEIPT_IS_INVALID));
    }

    @Test
    public void givenReceiptMissingPurchaseTime_validateReceipt_throwException()
            throws Exception {

        ClassPathResource resource = new ClassPathResource("examples/missing-purchase-time.json");

        mvc.perform(post("/receipts/process").content(resource.getContentAsByteArray())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(GlobalControllerAdvice.RECEIPT_IS_INVALID));
    }

    @Test
    public void givenReceiptMissingItems_validateReceipt_throwException()
            throws Exception {

        ClassPathResource resource = new ClassPathResource("examples/missing-items.json");

        mvc.perform(post("/receipts/process").content(resource.getContentAsByteArray())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(GlobalControllerAdvice.RECEIPT_IS_INVALID));
    }

    @Test
    public void givenReceiptMissingTotal_validateReceipt_throwException()
            throws Exception {

        ClassPathResource resource = new ClassPathResource("examples/missing-total.json");

        mvc.perform(post("/receipts/process").content(resource.getContentAsByteArray())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(GlobalControllerAdvice.RECEIPT_IS_INVALID));
    }

    @Test
    public void givenReceiptId_receiptNotFound_returnBadRequest()
            throws Exception {
        UUID id = UUID.randomUUID();

        given(receiptService.getReceiptPoints(id)).willThrow(new EntityNotFoundException(ReceiptProcessorService.RECEIPT_NOT_FOUND_MSG));

        mvc.perform(get("/receipts/"+id+"/points")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(ReceiptProcessorService.RECEIPT_NOT_FOUND_MSG));
    }


    @Test
    public void givenReceiptId_invalidId_returnNotFound()
            throws Exception {

        mvc.perform(get("/receipts/"+123+"/points")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(ReceiptProcessorService.RECEIPT_NOT_FOUND_MSG));
    }

}
