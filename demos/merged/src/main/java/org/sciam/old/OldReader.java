package org.sciam.old;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * The purpose of this code is to cound the number of lines
 * inside a file using Buffered Reader.
 */
public class OldReader {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        final String filePath = "store.data";
        long totalLines = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String id = line.substring(0, 25);
                String temperature = line.substring(25, 30);
                if (!id.isBlank() && !temperature.isBlank()) {
                    totalLines++;
                    if (totalLines % 100_000_000 == 0) {
                        System.out.println("Processing in progress " + (System.currentTimeMillis() - start) + " ms, lines: " + format(totalLines, 0));
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Reading finished in " + (System.currentTimeMillis() - start) + " ms, total lines: " + format(totalLines, 0));
    }

    // Format number with fixed decimal places (e.g., 1234.57)
    public static String format(double number, int decimalPlaces) {
        StringBuilder pattern = new StringBuilder("###,##0");
        if (decimalPlaces > 0) {
            pattern.append(".");
            pattern.append("0".repeat(decimalPlaces));
        }
        DecimalFormat df = new DecimalFormat(pattern.toString());
        return df.format(number);
    }
}
