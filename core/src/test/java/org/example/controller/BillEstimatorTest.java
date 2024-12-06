package org.example.controller;

import org.example.commons.model.Customer;
import org.example.commons.model.MasterPersistence;
import org.example.commons.model.TariffTax;
import org.example.client.view.BillEstimator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BillEstimatorTest {

    private Customer mockCustomer;
    private TariffTax mockTariffTax;
    private MasterPersistence mockMasterPersistence;
    private BillEstimator billEstimator;

    @BeforeEach
    void setup() {
        // Mock Customer
        mockCustomer = mock(Customer.class);
        when(mockCustomer.getRegularUnitsConsumed()).thenReturn(100.0F);
        when(mockCustomer.getPeakUnitsConsumed()).thenReturn(50.0F);

        // Mock TariffTax
        mockTariffTax = mock(TariffTax.class);
        when(mockTariffTax.getRegularUnitPrice()).thenReturn(10.0);
        when(mockTariffTax.getPeakHourUnitPrice()).thenReturn(15.0);
        when(mockTariffTax.getTaxPercentage()).thenReturn(5.0);
        when(mockTariffTax.getFixedCharges()).thenReturn(200.0);

        // Mock MasterPersistence and its getInstance method
        mockMasterPersistence = mock(MasterPersistence.class);
        ArrayList<TariffTax> tariffTaxList = new ArrayList<>();
        tariffTaxList.add(mockTariffTax);
        when(mockMasterPersistence.getTariffTaxes()).thenReturn(tariffTaxList);

        // Use MockedStatic for MasterPersistence and TariffTax
        try (MockedStatic<MasterPersistence> mockedMasterPersistence = Mockito.mockStatic(MasterPersistence.class);
             MockedStatic<TariffTax> mockedTariffTax = Mockito.mockStatic(TariffTax.class)) {

            mockedMasterPersistence.when(MasterPersistence::getInstance).thenReturn(mockMasterPersistence);
            mockedTariffTax.when(() -> TariffTax.getTariffTax(tariffTaxList, mockCustomer)).thenReturn(mockTariffTax);

            // Initialize BillEstimator
            billEstimator = new BillEstimator(mockCustomer);
        }
    }

    @Test
    void testBillEstimatorCalculations() {
        // Set test values
        double regularUnits = 150.0;
        double peakUnits = 80.0;

        // Set the JSpinner values
        billEstimator.getSpinRegularUnits().setValue(regularUnits);
        billEstimator.getSpinPeakUnits().setValue(peakUnits);

        // Trigger the calculations
        billEstimator.updateTotal();

        // Calculate expected values
        double expectedRegularUnitsCost = (regularUnits - mockCustomer.getRegularUnitsConsumed()) * mockTariffTax.getRegularUnitPrice();
        double expectedPeakUnitsCost = (peakUnits - mockCustomer.getPeakUnitsConsumed()) * mockTariffTax.getPeakHourUnitPrice();
        double expectedElectricityCost = expectedRegularUnitsCost + expectedPeakUnitsCost;
        double expectedSalesTax = expectedElectricityCost * (mockTariffTax.getTaxPercentage() / 100);
        double expectedFixedCharges = mockTariffTax.getFixedCharges();
        double expectedTotal = expectedElectricityCost + expectedSalesTax + expectedFixedCharges;

        // Assert the values displayed in the labels
        assertEquals("Electricity Cost: Rs. " + String.format("%.2f", expectedElectricityCost), billEstimator.getLblElectricityCost().getText());
        assertEquals("Sales Tax: Rs. " + String.format("%.2f", expectedSalesTax), billEstimator.getLblSalesTax().getText());
        assertEquals("Fixed Charges: Rs. " + String.format("%.2f", expectedFixedCharges), billEstimator.getLblFixedCharges().getText());
        assertEquals("Total Reading: Rs. " + String.format("%.2f", expectedTotal), billEstimator.getLblTotal().getText());
    }
}