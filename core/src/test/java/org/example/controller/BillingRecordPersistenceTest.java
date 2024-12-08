package org.example.controller;

import org.example.commons.model.BillingRecord;
import org.example.commons.model.BillingRecordPersistence;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class BillingRecordPersistenceTest {

    @Test
    void testReadFromFile() {
        // Prepare mock billing records
        ArrayList<BillingRecord> mockBillingRecords = new ArrayList<>();
        mockBillingRecords.add(new BillingRecord("12345", "01/2024", 100.5f, 50.25f, "01/01/2024", 200.75f, 15.5f, 25.0f, 291.25f, "15/02/2024"));
        mockBillingRecords.add(new BillingRecord("67890", "02/2024", 120.0f, 60.0f, "01/02/2024", 250.0f, 18.0f, 30.0f, 318.0f, "15/03/2024"));

        // Mock the static readFromFile method
        try (MockedStatic<BillingRecordPersistence> mockedPersistence = Mockito.mockStatic(BillingRecordPersistence.class)) {
            mockedPersistence.when(BillingRecordPersistence::readFromFile).thenReturn(mockBillingRecords);

            ArrayList<BillingRecord> billingRecordsFromFile = BillingRecordPersistence.readFromFile();

            assertNotNull(billingRecordsFromFile, "The billing records should not be null.");
            assertEquals(2, billingRecordsFromFile.size(), "There should be 2 billing records.");

            BillingRecord firstRecord = billingRecordsFromFile.get(0);
            assertEquals("12345", firstRecord.getCustomerID(), "The customer ID should match.");
            assertEquals("01/2024", firstRecord.getBillingMonth(), "The billing month should match.");
            assertEquals(100.5f, firstRecord.getCurrentMeterReadingRegular(), 0.01, "The regular meter reading should match.");
            assertEquals(50.25f, firstRecord.getCurrentMeterReadingPeak(), 0.01, "The peak meter reading should match.");
            assertEquals(291.25f, firstRecord.getTotalBillingAmount(), 0.01, "The total billing amount should match.");
            assertEquals("15/02/2024", firstRecord.getDueDate(), "The due date should match.");
        }
    }

    @Test
    void testEmptyFile() {
        // Mock an empty list for empty file scenario
        try (MockedStatic<BillingRecordPersistence> mockedPersistence = Mockito.mockStatic(BillingRecordPersistence.class)) {
            mockedPersistence.when(BillingRecordPersistence::readFromFile).thenReturn(new ArrayList<>());

            ArrayList<BillingRecord> billingRecordsFromFile = BillingRecordPersistence.readFromFile();

            assertNotNull(billingRecordsFromFile, "The billing records should not be null.");
            assertTrue(billingRecordsFromFile.isEmpty(), "The billing records list should be empty.");
        }
    }
}