package org.example.controller;

import org.example.commons.model.NADRARecord;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class NADRARecordTest {

    @Test
    void testUpdateCNICExpiryDate() {
        // Arrange
        ArrayList<NADRARecord> nadraRecords = new ArrayList<>();
        nadraRecords.add(new NADRARecord("12345-67890-1", "01/01/2020", "01/01/2030"));
        nadraRecords.add(new NADRARecord("98765-43210-1", "01/06/2020", "01/06/2030"));

        String targetCNIC = "12345-67890-1";
        String newExpiryDate = "15/12/2035";

        // Act
        for (NADRARecord record : nadraRecords) {
            if (record.getCNIC().equals(targetCNIC)) {
                record.setExpiryDate(newExpiryDate);
                break;
            }
        }

        NADRARecord updatedRecord = null;
        for (NADRARecord record : nadraRecords) {
            if (record.getCNIC().equals(targetCNIC)) {
                updatedRecord = record;
                break;
            }
        }


        assertNotNull(updatedRecord, "The target CNIC record should exist.");
        assertEquals(newExpiryDate, updatedRecord.getExpiryDate(), "The expiry date should be updated correctly.");
    }
}