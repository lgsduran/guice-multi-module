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
  with TestNG, JSON Schema validation, and Allure reports. See the
  [module README](viacep-module/README.md) for details.

## Project Structure

```text
guice-multi-module/
├── pom.xml
├── provider-module/
│   ├── pom.xml
│   ├── testng.xml
│   └── src/test/java/module/
│       ├── config/
│       │   ├── providers/
│       │   ├── qualifiers/
│       │   ├── SearchUrlModule.java
│       │   └── WebDriverModule.java
│       └── tests/
│           ├── ParallelSearchTest.java
│           └── ThreadLocalWebDriverProviderTest.java
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

The `singleton-module` explicit-wait timeout is configured in
`singleton-module/src/test/resources/config.properties` with the
`webdriver.wait.timeout.seconds` property.

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
