package module.reports;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import module.reports.ApiTestLog.TestExecution;

final class ApiTestPdfReport {
    private static final float MARGIN = 40;
    private static final float BODY_FONT_SIZE = 9;
    private static final float LINE_HEIGHT = 12;
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private ApiTestPdfReport() {
    }

    static void write(Path output, List<TestExecution> executions) throws IOException {
        Path absoluteOutput = output.toAbsolutePath();
        Files.createDirectories(absoluteOutput.getParent());

        try (PDDocument document = new PDDocument()) {
            ReportWriter writer = new ReportWriter(document);
            writer.write("Logs dos testes da API ViaCEP", true, 16);
            writer.write("Gerado em: " + OffsetDateTime.now(), false, BODY_FONT_SIZE);
            writer.write("", false, BODY_FONT_SIZE);

            if (executions.isEmpty()) {
                writer.write("Nenhum teste foi executado.", false, BODY_FONT_SIZE);
            }

            for (TestExecution execution : executions) {
                writer.write(execution.getName(), true, 11);
                if (execution.getDescription() != null && !execution.getDescription().isBlank()) {
                    writer.write("Descrição: " + execution.getDescription(), false, BODY_FONT_SIZE);
                }
                writer.write("Resultado: " + execution.getStatus(), true, BODY_FONT_SIZE);
                for (String message : execution.getMessages()) {
                    for (String line : message.split("\\R", -1)) {
                        writer.write(line, false, BODY_FONT_SIZE);
                    }
                }
                writer.write("", false, BODY_FONT_SIZE);
            }

            writer.close();
            document.save(absoluteOutput.toFile());
        }
    }

    private static final class ReportWriter {
        private final PDDocument document;
        private PDPageContentStream content;
        private float y;

        private ReportWriter(PDDocument document) throws IOException {
            this.document = document;
            newPage();
        }

        private void write(String text, boolean bold, float fontSize) throws IOException {
            PDType1Font font = bold ? PDType1Font.HELVETICA_BOLD : PDType1Font.HELVETICA;
            float maxWidth = PDRectangle.A4.getWidth() - 2 * MARGIN;
            String prefix = OffsetDateTime.now().format(TIMESTAMP_FORMAT) + " | ";
            float availableWidth = maxWidth - font.getStringWidth(prefix) * fontSize / 1000;
            float lineHeight = Math.max(LINE_HEIGHT, fontSize + 4);
            for (String line : wrap(text, font, fontSize, availableWidth)) {
                if (y - lineHeight < MARGIN) {
                    newPage();
                }
                content.beginText();
                content.setFont(font, fontSize);
                content.newLineAtOffset(MARGIN, y);
                content.showText(prefix + line);
                content.endText();
                y -= lineHeight;
            }
        }

        private List<String> wrap(String text, PDType1Font font, float fontSize, float maxWidth)
                throws IOException {
            if (text.isEmpty()) {
                return List.of("");
            }

            List<String> lines = new ArrayList<>();
            StringBuilder line = new StringBuilder();
            for (String originalWord : text.split("\\s+")) {
                String word = originalWord;
                while (!word.isEmpty()) {
                    String candidate = line.isEmpty() ? word : line + " " + word;
                    if (fits(candidate, font, fontSize, maxWidth)) {
                        line.setLength(0);
                        line.append(candidate);
                        break;
                    }
                    if (!line.isEmpty()) {
                        lines.add(line.toString());
                        line.setLength(0);
                        continue;
                    }

                    int splitAt = word.length();
                    while (splitAt > 1 && !fits(word.substring(0, splitAt), font, fontSize, maxWidth)) {
                        splitAt--;
                    }
                    lines.add(word.substring(0, splitAt));
                    word = word.substring(splitAt);
                }
            }
            if (!line.isEmpty()) {
                lines.add(line.toString());
            }
            return lines;
        }

        private boolean fits(String text, PDType1Font font, float fontSize, float maxWidth)
                throws IOException {
            return font.getStringWidth(text) * fontSize / 1000 <= maxWidth;
        }

        private void newPage() throws IOException {
            if (content != null) {
                content.close();
            }
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            content = new PDPageContentStream(document, page);
            y = PDRectangle.A4.getHeight() - MARGIN;
        }

        private void close() throws IOException {
            if (content != null) {
                content.close();
                content = null;
            }
        }
    }
}
