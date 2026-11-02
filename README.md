# Guice Multi-Module Project

A Maven study project with three independent modules demonstrating dependency
injection with Guice, UI testing with Selenium, and API testing with
Rest Assured. The `viacep-module` uses the public ViaCEP API, not the official
Brazilian postal service API.

## Modules

- **`provider-module`** — Guice `Provider`, a thread-local WebDriver pattern,
  and Selenium tests with TestNG.
- **`singleton-module`** — Singleton WebDriver, providers, qualifiers, a base
  test class, a screenshot extension, the Page Object pattern, and JUnit 5
  tests.
- **`viacep-module`** — Rest Assured configured with Guice, ViaCEP API tests
  with TestNG, JSON Schema validation, and Allure reports.

### Provider Module

- `WebDriverModule` binds Guice's `WebDriver` provider to
  `ThreadLocalWebDriverProvider`, which creates and reuses a ChromeDriver per
  TestNG worker thread.
- `quitDriver()` closes and removes the current thread's driver after each
  search test.
- `SearchUrlModule` loads and validates the Google and Bing URLs from
  `config.properties`, binding them with the `GoogleUrl` and `BingUrl`
  qualifiers.
- `ParallelSearchTest` runs the Google and Bing browser tests in parallel;
  `ThreadLocalWebDriverProviderTest` verifies driver isolation and reuse
  without launching a browser.

### Singleton Module

- `WebDriverModule` binds a singleton ChromeDriver, a configured `WebDriverWait`,
  and the Demoblaze base URL with Guice qualifiers.
- `BaseTest` initializes Guice and the browser before each JUnit 5 test, then
  closes the browser afterward.
- `AboutUsPage` uses the Page Object pattern to interact with and validate the
  About Us modal; `AboutUsTest` covers its fields and buttons.
- `ScreenshotExtension` attaches a screenshot to the Allure report when a test
  fails.
- `config.properties` configures the explicit-wait timeout.

### ViaCEP Module

- `ViaCepModule` loads and validates the endpoint configuration and defines the
  Guice bindings.
- `BaseTest` creates the Guice injector and injects the API configuration.
- `ViaCepRequestProvider` provides the Rest Assured `RequestSpecification`.
- `BaseUrl` and `CepEndpoint` are Guice qualifiers for the API URL and endpoint.
- `endpoints.yml` stores the API URL and CEP route; the JSON schema validates
  successful responses.

## Project Structure

```text
guice-multi-module/
├── pom.xml
├── provider-module/
│   ├── pom.xml
│   ├── testng.xml
│   └── src/test/
│       ├── java/module/
│       │   ├── config/
│       │   │   ├── providers/
│       │   │   ├── qualifiers/
│       │   │   ├── SearchUrlModule.java
│       │   │   └── WebDriverModule.java
│       │   └── tests/
│       │       ├── ParallelSearchTest.java
│       │       └── ThreadLocalWebDriverProviderTest.java
│       └── resources/
│           └── config.properties
├── singleton-module/
│   └── src/test/
│       ├── java/module/
│       │   ├── config/
│       │   │   ├── base/
│       │   │   ├── extensions/
│       │   │   ├── providers/
│       │   │   └── qualifiers/
│       │   ├── pages/
│       │   └── tests/
│       └── resources/
│           └── config.properties
└── viacep-module/
    ├── pom.xml
    ├── testng.xml
    └── src/test/
        ├── java/module/
        │   ├── config/
        │   │   ├── base/
        │   │   ├── providers/
        │   │   └── qualifiers/
        │   └── tests/
        └── resources/
            ├── endpoints.yml
            └── schemas/
```

## Prerequisites

- Java 17
- Maven
- Chrome installed for the Selenium tests
- Internet access for tests that call Google, Bing, and ViaCEP

## Running the Tests

Run these commands from the repository root.

To run an individual module:

```bash
# TestNG suite from provider-module/testng.xml
mvn -pl provider-module test

# JUnit + Selenium + Guice Singleton
mvn -pl singleton-module test

# TestNG suite from viacep-module/testng.xml + Rest Assured + ViaCEP
mvn -pl viacep-module test
```

Each TestNG module's Surefire configuration points to its `testng.xml` suite.
The `provider-module` suite includes a network-free
`ThreadLocalWebDriverProviderTest` and runs the
`ParallelSearchTest` methods on two TestNG threads; the `viacep-module` suite
selects `module.tests.ViaCepTest`. Therefore,
`mvn -pl provider-module test`, `mvn -pl viacep-module test`, and the CI's
`mvn verify` execute the corresponding TestNG suites. The `singleton-module`
continues to use JUnit 5.

The Provider, Singleton, and ViaCEP suites generate a timestamped execution PDF
under each module's `target` directory on every run. The browser modules include
screenshots as execution evidence; ViaCEP includes request and response logs.
The CI workflow includes these PDFs in its `test-reports` artifact. Configure
each output path with the `test.report.pdf.path` property in Provider and
Singleton, or `api.test.pdf.path` in ViaCEP, in that module's
`src/test/resources/config.properties`.

The `singleton-module` explicit-wait timeout is configured in
`singleton-module/src/test/resources/config.properties` with the
`webdriver.wait.timeout.seconds` property.
The `provider-module` Google and Bing URLs are configured in
`provider-module/src/test/resources/config.properties` with the `google.url`
and `bing.url` properties.

To run all tests:

```bash
mvn test
```

To run the full Maven reactor verification:

```bash
mvn verify
```

GitHub Actions runs `mvn verify` on pushes and pull requests using Ubuntu
24.04, Java 17, and Chrome. This runs the TestNG suites configured by
`provider-module/testng.xml` and `viacep-module/testng.xml`, plus the JUnit
suite in `singleton-module`. Surefire and Allure reports are available as
workflow artifacts.

## Allure Reports

Each module writes its own Allure results. Run its tests, then generate that
module's HTML report with the Allure CLI:

```bash
# provider-module
mvn -pl provider-module test
allure generate provider-module/target/allure-results \
  --output provider-module/target/allure-report --clean

# singleton-module
mvn -pl singleton-module test
allure generate singleton-module/target/allure-results \
  --output singleton-module/target/allure-report --clean

# viacep-module
mvn -pl viacep-module test
allure generate viacep-module/target/allure-results \
  --output viacep-module/target/allure-report --clean
```
