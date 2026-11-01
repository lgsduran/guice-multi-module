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
- **`viacep-module`** — Rest Assured configured with Guice, ViaCEP API tests,
  JSON Schema validation, and Allure reports. See the
  [module README](viacep-module/README.md) for details.

## Project Structure

```text
guice-multi-module/
├── pom.xml
├── provider-module/
│   └── src/test/java/module/
│       ├── config/
│       └── tests/
├── singleton-module/
│   └── src/test/java/module/
│       ├── config/
│       │   ├── base/
│       │   ├── extensions/
│       │   ├── providers/
│       │   └── qualifiers/
│       ├── pages/
│       └── tests/
└── viacep-module/
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
# TestNG + Selenium + Guice Provider
mvn -pl provider-module test

# JUnit + Selenium + Guice Singleton
mvn -pl singleton-module test

# JUnit + Rest Assured + ViaCEP
mvn -pl viacep-module test
```

To run all tests:

```bash
mvn test
```

To run the full Maven reactor verification:

```bash
mvn verify
```

GitHub Actions runs `mvn verify` on pushes and pull requests using Ubuntu
24.04, Java 17, and Chrome. Surefire and Allure reports are available as
workflow artifacts.

## Allure Report

After running the `viacep-module` tests, generate the HTML report with the
Allure CLI:

```bash
allure generate viacep-module/target/allure-results \
  --output viacep-module/target/allure-report --clean
```
