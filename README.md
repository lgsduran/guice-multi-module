# Projeto multi-módulo Guice

Projeto Maven de estudo com três módulos independentes, demonstrando injeção
de dependências com Guice, testes de UI com Selenium e testes de API com
Rest Assured. O `viacep-module` usa a API pública ViaCEP, não a API oficial dos
Correios.

## Módulos

- **`provider-module`** — Guice `Provider`, WebDriver isolado por thread e
  testes Selenium com TestNG.
- **`singleton-module`** — WebDriver singleton, providers, qualifiers, classe
  base, extensão de screenshots, Page Object e testes JUnit 5.
- **`viacep-module`** — Rest Assured configurado com Guice, testes da API
  ViaCEP, validação de JSON Schema e relatórios Allure. Consulte o
  [README do módulo](viacep-module/README.md) para detalhes.

## Estrutura

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

## Pré-requisitos

- Java 17
- Maven
- Chrome instalado para os testes Selenium
- Acesso à internet para os testes que consultam Google, Bing e ViaCEP

## Executar os testes

Execute os comandos na raiz do repositório.

Para rodar cada módulo:

```bash
# TestNG + Selenium + Guice Provider
mvn -pl provider-module test

# JUnit + Selenium + Guice Singleton
mvn -pl singleton-module test

# JUnit + Rest Assured + ViaCEP
mvn -pl viacep-module test
```

Para rodar todos os testes:

```bash
mvn test
```

Para executar a verificação completa do reactor:

```bash
mvn verify
```

O GitHub Actions executa `mvn verify` em pushes e pull requests, usando
Ubuntu 24.04, Java 17 e Chrome. Os relatórios Surefire e Allure ficam
disponíveis como artefatos da execução.

## Relatório Allure

Após executar os testes do `viacep-module`, gere o relatório HTML com a CLI do
Allure:

```bash
allure generate viacep-module/target/allure-results \
  --output viacep-module/target/allure-report --clean
```
