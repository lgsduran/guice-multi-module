package module.reports;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

final class ExecutionEvidence {
    private final String name;
    private final String description;
    private String status = "EM EXECUÇÃO";
    private String details = "";
    private byte[] screenshot;
    private String screenshotDescription;

    ExecutionEvidence(String name, String description) {
        this.name = name;
        this.description = description;
    }

    void finish(String status, Throwable failure) {
        this.status = status;
        if (failure != null) {
            StringWriter stackTrace = new StringWriter();
            failure.printStackTrace(new PrintWriter(stackTrace));
            details = stackTrace.toString();
        }
    }

    void addScreenshot(byte[] image, String description) {
        screenshot = image;
        screenshotDescription = description;
    }

    void addEvidenceError(String error) {
        details += (details.isEmpty() ? "" : System.lineSeparator())
                + "Falha ao capturar evidência: " + error;
    }

    void clearScreenshot() {
        if (screenshot != null) {
            Arrays.fill(screenshot, (byte) 0);
            screenshot = null;
        }
        screenshotDescription = null;
    }

    String getName() {
        return name;
    }

    String getDescription() {
        return description;
    }

    String getStatus() {
        return status;
    }

    String getDetails() {
        return details;
    }

    byte[] getScreenshot() {
        return screenshot;
    }

    String getScreenshotDescription() {
        return screenshotDescription;
    }
}
