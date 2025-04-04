package org.example;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "org.example") // Cập nhật package cho phù hợp
public class AppConfig {
        @Bean(name = "plainTextWriter") // Đặt tên cho bean này
        public TextWriter plainTextWriter() {
            return new PlainTextWriter();
        }

        @Bean(name = "pdfTextWriter") // Đặt tên cho bean này
        public TextWriter pdfTextWriter() {
            return new PdfTextWriter();
        }

        @Bean
        public TextEditor textEditor(@Qualifier("pdfTextWriter") TextWriter writer) {
            return new TextEditor(writer);
        }
}

