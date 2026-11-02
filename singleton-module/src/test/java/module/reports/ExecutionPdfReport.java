package module.reports;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

final class ExecutionPdfReport {
    private static final float MARGIN = 40;
    private static final float FONT_SIZE = 9;
    private static final float LINE_HEIGHT = 12;
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

    private ExecutionPdfReport() {
    }

    static void write(Path output, List<ExecutionEvidence> executions) throws IOException {
        Path absoluteOutput = output.toAbsolutePath();
        Files.createDirectories(absoluteOutput.getParent());

        try (PDDocument document = new PDDocument()) {
            Writer writer = new Writer(document);
            writer.write("Relatório de execuções - Singleton Module", true, 16);
            writer.write("Gerado em: " + OffsetDateTime.now(), false, FONT_SIZE);
            writer.write("", false, FONT_SIZE);
            if (executions.isEmpty()) {
                writer.write("Nenhum teste foi executado.", false, FONT_SIZE);
            }
            for (ExecutionEvidence execution : executions) {
                writer.write(execution.getName(), true, 11);
                if (execution.getDescription() != null && !execution.getDescription().isBlank()) {
                    writer.write("Descrição: " + execution.getDescription(), false, FONT_SIZE);
                }
                writer.write("Resultado: " + execution.getStatus(), true, FONT_SIZE);
                if (!execution.getDetails().isBlank()) {
                    writer.write("Detalhes:", true, FONT_SIZE);
                    writer.writeLines(execution.getDetails());
                }
                if (execution.getScreenshot() != null) {
                    writer.write("Evidência: " + execution.getScreenshotDescription(), true, FONT_SIZE);
                    writer.writeImage(execution.getScreenshot());
                }
                writer.write("", false, FONT_SIZE);
            }
            writer.close();
            document.save(absoluteOutput.toFile());
        }
    }

    private static final class Writer {
        private final PDDocument document;
        private PDPageContentStream content;
        private float y;

        private Writer(PDDocument document) throws IOException {
            this.document = document;
            newPage();
        }

        private void writeLines(String text) throws IOException {
            for (String line : text.split("\\R", -1)) {
                write(line, false, FONT_SIZE);
            }
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

        private void writeImage(byte[] bytes) throws IOException {
            PDImageXObject image = PDImageXObject.createFromByteArray(document, bytes, "screenshot");
            float maxWidth = PDRectangle.A4.getWidth() - 2 * MARGIN;
            float maxHeight = PDRectangle.A4.getHeight() - 2 * MARGIN;
            float scale = Math.min(maxWidth / image.getWidth(), maxHeight / image.getHeight());
            float width = image.getWidth() * scale;
            float height = image.getHeight() * scale;
            if (y - height < MARGIN) {
                newPage();
            }
            content.drawImage(image, MARGIN, y - height, width, height);
            y -= height + LINE_HEIGHT;
        }

        private List<String> wrap(String text, PDType1Font font, float fontSize, float maxWidth)
                throws IOException {
            if (text.isEmpty()) {
                return List.of("");
            }
            java.util.ArrayList<String> lines = new java.util.ArrayList<>();
            StringBuilder line = new StringBuilder();
            for (String word : text.split("\\s+")) {
                String candidate = line.isEmpty() ? word : line + " " + word;
                if (font.getStringWidth(candidate) * fontSize / 1000 <= maxWidth) {
                    line.setLength(0);
                    line.append(candidate);
                } else if (!line.isEmpty()) {
                    lines.add(line.toString());
                    line.setLength(0);
                    line.append(word);
                } else {
                    lines.add(word);
                }
            }
            if (!line.isEmpty()) {
                lines.add(line.toString());
            }
            return lines;
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
