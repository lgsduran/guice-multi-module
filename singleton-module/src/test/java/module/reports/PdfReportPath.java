package module.reports;

import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

final class PdfReportPath {
    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    private PdfReportPath() {
    }

    static Path withTimestamp(Path path) {
        Path fileName = path.getFileName();
        if (fileName == null) {
            throw new IllegalArgumentException("O caminho do relatório deve incluir o nome do arquivo.");
        }

        String name = fileName.toString();
        int extensionIndex = name.lastIndexOf('.');
        String timestamp = OffsetDateTime.now().format(TIMESTAMP_FORMAT);
        String timestampedName = extensionIndex > 0
                ? name.substring(0, extensionIndex) + "-" + timestamp + name.substring(extensionIndex)
                : name + "-" + timestamp;
        Path parent = path.getParent();
        return parent == null ? Path.of(timestampedName) : parent.resolve(timestampedName);
    }
}
