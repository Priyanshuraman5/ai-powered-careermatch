package com.careermatch.service;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Extracts raw text from uploaded resume files (PDF, DOCX, or plain text).
 * Kept separate from ResumeService so the file-format concern is isolated from the
 * business-logic / persistence concern.
 */
@Service
public class ResumeParsingService {

    public String extractText(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();

        if (filename.endsWith(".pdf")) {
            return extractFromPdf(file.getInputStream());
        } else if (filename.endsWith(".docx")) {
            return extractFromDocx(file.getInputStream());
        } else {
            // fall back to plain text (.txt or unknown types)
            return new String(file.getBytes());
        }
    }

    private String extractFromPdf(InputStream inputStream) throws IOException {
        try (var document = Loader.loadPDF(inputStream.readAllBytes())) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private String extractFromDocx(InputStream inputStream) throws IOException {
        try (XWPFDocument doc = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
            return extractor.getText();
        }
    }
}
