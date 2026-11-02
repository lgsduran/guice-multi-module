package module.reports;

import java.io.IOException;
import java.nio.file.Path;

import com.google.inject.Guice;
import com.google.inject.Key;

import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import module.config.DefaultPdfPathModule;
import module.config.qualifiers.DefaultPdfPath;

public final class ApiTestPdfListener implements ITestListener, ISuiteListener {
    private static final String PDF_PATH_PROPERTY = "api.test.pdf.path";

    @Override
    public void onStart(ISuite suite) {
        ApiTestLog.clear();
    }

    @Override
    public void onTestStart(ITestResult result) {
        ApiTestLog.startTest(
                result.getTestClass().getName() + "." + result.getName(),
                result.getMethod().getDescription());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        finishTest(result, "PASSOU");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        finishTest(result, "FALHOU");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        finishTest(result, "IGNORADO");
    }

    @Override
    public void onFinish(ISuite suite) {
        Path output = null;
        try {
            output = PdfReportPath.withTimestamp(Path.of(loadConfiguredPdfPath()));
            ApiTestPdfReport.write(output, ApiTestLog.getExecutions());
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível gerar o relatório PDF em " + output, e);
        } finally {
            ApiTestLog.clear();
        }
    }

    private static String loadConfiguredPdfPath() {
        String configuredPath = Guice.createInjector(new DefaultPdfPathModule())
                .getInstance(Key.get(String.class, DefaultPdfPath.class));
        return System.getProperty(PDF_PATH_PROPERTY, configuredPath);
    }

    private static void finishTest(ITestResult result, String status) {
        ApiTestLog.finishTest(status, result.getThrowable());
    }
}
