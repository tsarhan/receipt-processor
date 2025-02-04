package com.purchase.rewards.receipt.processor.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Properties;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.purchase.rewards.receipt.processor.model.dto.ReceiptDTO;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@TestInstance(Lifecycle.PER_CLASS)
public class ReceiptPointCalculatorTest {

    private ReceiptPointCalculator receiptPointCalculator;

    private ObjectMapper objectMapper;

    private Properties properties = new Properties();

    @BeforeAll
    public void loadPropertiesFile() throws IOException {
        ClassPathResource resource = new ClassPathResource("application-test.properties");
        properties.load(resource.getInputStream());
    }

    @BeforeEach
    public void initTest() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        receiptPointCalculator = new ReceiptPointCalculator();
        ReflectionTestUtils.setField(receiptPointCalculator, "oddDayPoints", Integer.parseInt(properties.getProperty("odd.day.points")));
        ReflectionTestUtils.setField(receiptPointCalculator, "timePoints", Integer.parseInt(properties.getProperty("purchase.time.range.points")));
        ReflectionTestUtils.setField(receiptPointCalculator, "itemPairPoints", Integer.parseInt(properties.getProperty("item.pair.points")));
        ReflectionTestUtils.setField(receiptPointCalculator, "itemDescriptionLengthMutiple", Integer.parseInt(properties.getProperty("item.description.length.multiple.of")));
        ReflectionTestUtils.setField(receiptPointCalculator, "itemPriceMultipler", Float.parseFloat(properties.getProperty("item.price.multiplier")));
        ReflectionTestUtils.setField(receiptPointCalculator, "purchaseTimeRangeStart", LocalTime.parse(properties.getProperty("purchase.time.range.start")));
        ReflectionTestUtils.setField(receiptPointCalculator, "purchaseTimeRangeEnd", LocalTime.parse(properties.getProperty("purchase.time.range.end")));
        ReflectionTestUtils.setField(receiptPointCalculator, "totalPurchaseMultipleOf", Float.parseFloat(properties.getProperty("total.purchase.multiple.of")));
        ReflectionTestUtils.setField(receiptPointCalculator,"totalNoCentsPoints", Integer.parseInt(properties.getProperty("total.no.cents.points")));
        ReflectionTestUtils.setField(receiptPointCalculator,"totalPurchaseMultipleOfPoints", Integer.parseInt(properties.getProperty("total.purchase.multiple.of.points")));
    }

    @Test
    public void receiveReceipt_calculateTargetPoints_returnPoints() throws StreamReadException, DatabindException, IOException {
        ReceiptDTO receiptDTO = loadReceiptDTO("examples/target.json");

        assertEquals(28, receiptPointCalculator.calculatePoints(receiptDTO));
    }

    @Test
    public void receiveReceipt_calculateCornerMarketPoints_returnPoints() throws StreamReadException, DatabindException, IOException {
        ReceiptDTO receiptDTO = loadReceiptDTO("examples/corner-market.json");

        assertEquals(109, receiptPointCalculator.calculatePoints(receiptDTO));
    }

    @Test
    public void receiveReceipt_calculateSimplePoints_returnPoints() throws StreamReadException, DatabindException, IOException {
        ReceiptDTO receiptDTO = loadReceiptDTO("examples/simple-receipt.json");

        assertEquals(37, receiptPointCalculator.calculatePoints(receiptDTO));
    }

    @Test
    public void receiveReceipt_calculateMorningPoints_returnPoints() throws StreamReadException, DatabindException, IOException {
        ReceiptDTO receiptDTO = loadReceiptDTO("examples/morning-receipt.json");

        assertEquals(21, receiptPointCalculator.calculatePoints(receiptDTO));
    }

    private ReceiptDTO loadReceiptDTO(String path) throws StreamReadException, DatabindException, IOException {
        ClassPathResource resource = new ClassPathResource(path);
        
        return objectMapper.readValue(resource.getContentAsByteArray(), ReceiptDTO.class);
    }

}
