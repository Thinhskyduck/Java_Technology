package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class TextEditor {
    private final TextWriter writer;
    @Autowired
    public TextEditor(@Qualifier("pdfTextWriter") TextWriter writer) {
        this.writer = writer;
    }

    public void input(String text) {
        System.out.println("Input received: " + text);
    }

    public void save(String fileName) {
        writer.write(fileName, "Sample Text");
    }
}


