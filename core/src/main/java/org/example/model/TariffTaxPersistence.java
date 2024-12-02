package org.example.model;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TariffTaxPersistence {

    private static final String FILENAME = "core/src/main/java/org/example/model/TariffTaxInfo.txt";

    public static void writeToFile(List<TariffTax> tariffs) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILENAME))) {
            for (TariffTax tariffTax : tariffs) {
                bw.write(tariffTax.toFileString());
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static ArrayList<TariffTax> readFromFile() {
        ArrayList<TariffTax> tariffList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            int lineNum = 0;
            while ((line = br.readLine()) != null) {

                TariffTax tariffTax = parseTariffTax(line, lineNum);
                tariffList.add(tariffTax);
                lineNum++;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error!", JOptionPane.ERROR_MESSAGE);
        }

        return tariffList;
    }

    private static TariffTax parseTariffTax(String line, int lineNum) {
        String[] data = line.split(",");

        String meterType = data[0];
        String customerType = (lineNum % 2 == 0) ? "Domestic" : "Commercial";
        double regularUnitPrice = Double.parseDouble(data[1]);
        Double peakHourUnitPrice = (data[2].isEmpty()) ? null : Double.parseDouble(data[2]);
        double taxPercentage = Double.parseDouble(data[3]);
        double fixedCharges = Double.parseDouble(data[4]);

        return new TariffTax(meterType, customerType, regularUnitPrice, peakHourUnitPrice, taxPercentage, fixedCharges);
    }
}
