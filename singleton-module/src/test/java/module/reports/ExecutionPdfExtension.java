package module.reports;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.inject.Guice;
import com.google.inject.Key;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import module.config.DefaultPdfPathModule;
import module.config.qualifiers.DefaultPdfPath;

public final class ExecutionPdfExtension implements
        BeforeAllCallback, AfterTestExecutionCallback, TestWatcher, AfterAllCallback {
    private static final String PDF_PATH_PROPERTY = "test.report.pdf.path";

    private final ConcurrentMap<String, ExecutionEvidence> executions = new ConcurrentHashMap<>();

    @Override
    public void beforeAll(ExtensionContext context) {
        executions.clear();
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        ExecutionEvidence execution = executionFor(context);
        try {
            WebDriver driver = findDriver(context.getRequiredTestInstance());
            if (driver != null) {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                execution.addScreenshot(screenshot,
                        "URL: " + driver.getCurrentUrl() + " | Título: " + driver.getTitle());
            }
        } catch (ReflectiveOperationException | WebDriverException e) {
            execution.addEvidenceError(e.getMessage());
        }
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        finish(context, "PASSOU", null);
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        finish(context, "FALHOU", cause);
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        finish(context, "ABORTADO", cause);
    }

    @Override
    public void testDisabled(ExtensionContext context, java.util.Optional<String> reason) {
        ExecutionEvidence execution = executionFor(context);
        execution.finish("DESABILITADO", null);
        reason.ifPresent(message -> execution.addEvidenceError("Teste desabilitado: " + message));
    }

    @Override
    public void afterAll(ExtensionContext context) {
        Path output = null;
        List<ExecutionEvidence> reportExecutions = new ArrayList<>(executions.values());
        try {
            output = PdfReportPath.withTimestamp(Path.of(loadConfiguredPdfPath()));
            ExecutionPdfReport.write(output, reportExecutions);
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível gerar o relatório PDF em " + output, e);
        } finally {
            reportExecutions.forEach(ExecutionEvidence::clearScreenshot);
            executions.clear();
        }
    }

    private void finish(ExtensionContext context, String status, Throwable failure) {
        executionFor(context).finish(status, failure);
    }

    private ExecutionEvidence executionFor(ExtensionContext context) {
        return executions.computeIfAbsent(context.getUniqueId(), ignored ->
                new ExecutionEvidence(
                        context.getRequiredTestClass().getName() + "." + context.getRequiredTestMethod().getName(),
                        context.getDisplayName()));
    }

    private static WebDriver findDriver(Object testInstance) throws ReflectiveOperationException {
        for (Class<?> type = testInstance.getClass(); type != null; type = type.getSuperclass()) {
            try {
                Field driverField = type.getDeclaredField("driver");
                driverField.setAccessible(true);
                return (WebDriver) driverField.get(testInstance);
            } catch (NoSuchFieldException ignored) {
                // Continue searching the test class hierarchy.
            }
        }
        return null;
    }

    private static String loadConfiguredPdfPath() {
        String configuredPath = Guice.createInjector(new DefaultPdfPathModule())
                .getInstance(Key.get(String.class, DefaultPdfPath.class));
        return System.getProperty(PDF_PATH_PROPERTY, configuredPath);
    }
}
