package module.reports;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public final class ApiTestLog {
    private static final List<TestExecution> EXECUTIONS = new ArrayList<>();
    private static final ThreadLocal<TestExecution> CURRENT_EXECUTION = new ThreadLocal<>();

    private ApiTestLog() {
    }

    public static void clear() {
        synchronized (EXECUTIONS) {
            EXECUTIONS.clear();
        }
        CURRENT_EXECUTION.remove();
    }

    public static void startTest(String name, String description) {
        CURRENT_EXECUTION.set(new TestExecution(name, description));
    }

    public static void record(String message) {
        TestExecution execution = CURRENT_EXECUTION.get();
        if (execution == null) {
            throw new IllegalStateException("Não há um teste ativo para registrar o log da API.");
        }
        execution.messages.add(message);
    }

    public static void finishTest(String status, Throwable failure) {
        TestExecution execution = CURRENT_EXECUTION.get();
        if (execution == null) {
            throw new IllegalStateException("Não há um teste ativo para concluir o registro.");
        }
        execution.status = status;
        if (failure != null) {
            StringWriter stackTrace = new StringWriter();
            failure.printStackTrace(new PrintWriter(stackTrace));
            execution.messages.add("Falha: " + stackTrace);
        }
        synchronized (EXECUTIONS) {
            EXECUTIONS.add(execution);
        }
        CURRENT_EXECUTION.remove();
    }

    public static List<TestExecution> getExecutions() {
        synchronized (EXECUTIONS) {
            return List.copyOf(EXECUTIONS);
        }
    }

    public static final class TestExecution {
        private final String name;
        private final String description;
        private final List<String> messages = new ArrayList<>();
        private String status;

        private TestExecution(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public List<String> getMessages() {
            return List.copyOf(messages);
        }

        public String getStatus() {
            return status;
        }
    }
}
