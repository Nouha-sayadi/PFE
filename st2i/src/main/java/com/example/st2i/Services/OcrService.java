package com.example.st2i.Services;

import com.example.st2i.Exception.OcrExtractionException;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Extraction de texte brut depuis un PDF ou une image.
 * Pour un PDF : tente d'abord l'extraction directe du texte (PDF numérique) ;
 * si le texte obtenu est insuffisant (PDF scanné, sans couche texte), bascule
 * sur un OCR Tesseract page par page.
 */
@Service
public class OcrService {

    @Value("${app.ocr.tessdata-path}")
    private String tessdataPath;

    @Value("${app.ocr.languages}")
    private String languages;

    private static final int MIN_TEXT_LENGTH_PER_PAGE = 30;
    private static final float RENDER_DPI = 300f;

    public String extractText(MultipartFile file) {
        String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename().toLowerCase() : "";
        try {
            if (fileName.endsWith(".pdf")) {
                return extractFromPdf(file.getBytes());
            } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")) {
                BufferedImage image = ImageIO.read(file.getInputStream());
                if (image == null) {
                    throw new OcrExtractionException("Image illisible.");
                }
                return runTesseract(image);
            } else {
                throw new OcrExtractionException(
                        "Format non supporté pour l'extraction automatique (PDF, JPG, PNG uniquement).");
            }
        } catch (IOException e) {
            throw new OcrExtractionException("Échec de la lecture du fichier.", e);
        }
    }

    private String extractFromPdf(byte[] bytes) {
        try (PDDocument document = Loader.loadPDF(bytes)) {
            int pageCount = document.getNumberOfPages();
            String text = new PDFTextStripper().getText(document).trim();

            if (text.length() >= MIN_TEXT_LENGTH_PER_PAGE * pageCount) {
                return text;
            }

            // PDF scanné ou sans couche texte exploitable : fallback OCR page par page
            PDFRenderer renderer = new PDFRenderer(document);
            StringBuilder ocrText = new StringBuilder();
            for (int page = 0; page < pageCount; page++) {
                BufferedImage image = renderer.renderImageWithDPI(page, RENDER_DPI);
                ocrText.append(runTesseract(image)).append("\n");
            }
            return ocrText.toString().trim();
        } catch (IOException e) {
            throw new OcrExtractionException("Échec de la lecture du PDF.", e);
        }
    }

    private String runTesseract(BufferedImage image) {
        ITesseract tesseract = new Tesseract();
        tesseract.setDatapath(tessdataPath);
        tesseract.setLanguage(languages);
        try {
            return tesseract.doOCR(image);
        } catch (TesseractException e) {
            throw new OcrExtractionException("Échec de l'OCR sur le document.", e);
        }
    }
}
