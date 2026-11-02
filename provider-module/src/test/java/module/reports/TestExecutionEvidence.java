package module.reports;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.testng.ITestResult;

final class TestExecutionEvidence {
    static final String SCREENSHOT_ATTRIBUTE = "execution.screenshot";
    static final String SCREENSHOT_DESCRIPTION_ATTRIBUTE = "execution.screenshot.description";
    static final String EVIDENCE_ERROR_ATTRIBUTE = "execution.evidence.error";

    private final String name;
    private final String status;
    private final String details;
    private final byte[] screenshot;
    private final String screenshotDescription;

    private TestExecutionEvidence(
            String name, String status, String details, byte[] screenshot, String screenshotDescription) {
        this.name = name;
        this.status = status;
        this.details = details;
        this.screenshot = screenshot;
        this.screenshotDescription = screenshotDescription;
    }

    static TestExecutionEvidence from(ITestResult result) {
        String details = "";
        Throwable failure = result.getThrowable();
        if (failure != null) {
            StringWriter stackTrace = new StringWriter();
            failure.printStackTrace(new PrintWriter(stackTrace));
            details = stackTrace.toString();
        }

        Object evidenceError = result.getAttribute(EVIDENCE_ERROR_ATTRIBUTE);
        if (evidenceError != null) {
            details += (details.isEmpty() ? "" : System.lineSeparator())
                    + "Falha ao capturar evidência: " + evidenceError;
        }

        Object capturedScreenshot = result.getAttribute(SCREENSHOT_ATTRIBUTE);
        Object description = result.getAttribute(SCREENSHOT_DESCRIPTION_ATTRIBUTE);
        return new TestExecutionEvidence(
                result.getTestClass().getName() + "." + result.getName(),
                status(result.getStatus()),
                details,
                capturedScreenshot instanceof byte[] bytes ? bytes : null,
                description instanceof String text ? text : null);
    }

    private static String status(int status) {
        return switch (status) {
            case ITestResult.SUCCESS -> "PASSOU";
            case ITestResult.FAILURE -> "FALHOU";
            case ITestResult.SKIP -> "IGNORADO";
            default -> "DESCONHECIDO";
        };
    }

    String getName() {
        return name;
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

    static List<TestExecutionEvidence> snapshot(List<ITestResult> results) {
        List<TestExecutionEvidence> executions = new ArrayList<>();
        for (ITestResult result : results) {
            executions.add(from(result));
        }
        return executions;
    }
}
