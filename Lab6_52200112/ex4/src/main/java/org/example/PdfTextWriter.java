package org.example;

import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;

@Component("pdfTextWriter")
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

