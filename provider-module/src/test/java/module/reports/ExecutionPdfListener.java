package module.reports;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.inject.Guice;
import com.google.inject.Key;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import module.config.providers.ThreadLocalWebDriverProvider;
import module.config.DefaultPdfPathModule;
import module.config.qualifiers.DefaultPdfPath;

public final class ExecutionPdfListener implements ITestListener, ISuiteListener {
    private static final String PDF_PATH_PROPERTY = "test.report.pdf.path";
    private static final List<ITestResult> RESULTS = new CopyOnWriteArrayList<>();

    @Override
    public void onStart(ISuite suite) {
        RESULTS.clear();
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        RESULTS.add(result);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        RESULTS.add(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        RESULTS.add(result);
    }

    @Override
    public void onFinish(ISuite suite) {
        Path output = null;
        try {
            output = PdfReportPath.withTimestamp(Path.of(loadConfiguredPdfPath()));
            ExecutionPdfReport.write(output, RESULTS);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível gerar o relatório PDF em " + output, e);
        } finally {
            clearScreenshotData();
        }
    }

    public static void captureScreenshot(ITestResult result) {
        try {
            WebDriver driver = ThreadLocalWebDriverProvider.currentDriver();
            if (driver == null) {
                return;
            }
            result.setAttribute(TestExecutionEvidence.SCREENSHOT_ATTRIBUTE,
                    ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
            result.setAttribute(TestExecutionEvidence.SCREENSHOT_DESCRIPTION_ATTRIBUTE,
                    "URL: " + driver.getCurrentUrl() + " | Título: " + driver.getTitle());
        } catch (WebDriverException e) {
            result.setAttribute(TestExecutionEvidence.EVIDENCE_ERROR_ATTRIBUTE, e.getMessage());
        }
    }

    private static String loadConfiguredPdfPath() {
        String configuredPath = Guice.createInjector(new DefaultPdfPathModule())
                .getInstance(Key.get(String.class, DefaultPdfPath.class));
        return System.getProperty(PDF_PATH_PROPERTY, configuredPath);
    }

    private static void clearScreenshotData() {
        for (ITestResult result : RESULTS) {
            Object screenshot = result.getAttribute(TestExecutionEvidence.SCREENSHOT_ATTRIBUTE);
            if (screenshot instanceof byte[] bytes) {
                Arrays.fill(bytes, (byte) 0);
            }
            result.removeAttribute(TestExecutionEvidence.SCREENSHOT_ATTRIBUTE);
            result.removeAttribute(TestExecutionEvidence.SCREENSHOT_DESCRIPTION_ATTRIBUTE);
            result.removeAttribute(TestExecutionEvidence.EVIDENCE_ERROR_ATTRIBUTE);
        }
        RESULTS.clear();
    }
}
