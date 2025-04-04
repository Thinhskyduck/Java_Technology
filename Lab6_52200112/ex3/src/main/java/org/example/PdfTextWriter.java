package org.example;

import java.io.FileWriter;
import java.io.IOException;

public class PdfTextWriter implements TextWriter {
    @Override
    public void write(String fileName, String text) {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(text);
            System.out.println("Printing in PDF format: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

