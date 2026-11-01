# Projeto multi-módulo Guice

Projeto Maven de estudo com três módulos independentes. O `viacep-module`
integra Guice, Rest Assured e a API pública ViaCEP (não é a API oficial dos
Correios).

## Estrutura do projeto

```text
guice-multi-module/
├── pom.xml
├── provider-module/
│   ├── pom.xml
│   └── src/test/java/module/
│       ├── config/
│       │   ├── ThreadLocalWebDriverProvider.java
│       │   └── WebDriverModule.java
│       └── tests/
│           └── ParallelSearchTest.java
├── singleton-module/
│   ├── pom.xml
│   └── src/test/java/module/
│       ├── config/
│       │   ├── base/
│       │   ├── extensions/
│       │   ├── providers/
│       │   ├── qualifiers/
│       │   └── WebDriverModule.java
│       ├── pages/
│       └── tests/
└── viacep-module/
    ├── pom.xml
    ├── README.md
    └── src/test/
        ├── java/module/
        │   ├── config/
        │   │   ├── base/
        │   │   ├── providers/
        │   │   ├── qualifiers/
        │   │   └── ViaCepModule.java
        │   └── tests/
        │       └── ViaCepTest.java
        └── resources/
            ├── endpoints.yml
            └── schemas/
                └── viacep-response-schema.json
```

## Módulos

- `provider-module`: demonstra Guice `Provider` com WebDriver isolado por thread
  e testes paralelos com TestNG.
- `singleton-module`: demonstra WebDriver singleton, providers, qualifiers,
  classe-base, extensão de screenshots, Page Object e testes JUnit 5.
- `viacep-module`: configura Rest Assured com Guice e testa a API ViaCEP,
  incluindo validação de JSON Schema e relatórios Allure.

### Estrutura interna do `viacep-module`

- `config/ViaCepModule.java`: carrega e valida o YAML e registra os bindings
  Guice.
- `config/base/BaseTest.java`: cria o injector e injeta a configuração HTTP.
- `config/providers/ViaCepRequestProvider.java`: fornece a
  `RequestSpecification` do Rest Assured.
- `config/qualifiers/BaseUrl.java`: qualifier Guice para a URL base.
- `tests/ViaCepTest.java`: valida CEP válido, inexistente e formato inválido.
- `resources/endpoints.yml`: URL base e endpoints da API.
- `resources/schemas/viacep-response-schema.json`: schema da resposta de CEP.

## Executar

Execute os comandos na raiz do repositório. Para rodar cada módulo:

```bash
# TestNG + Selenium + Guice Provider
mvn -pl provider-module test

# JUnit + Selenium + Guice Singleton
mvn -pl singleton-module test

# JUnit + Rest Assured + ViaCEP
mvn -pl viacep-module test
```

Para executar os testes de todos os módulos:

```bash
mvn test
```

Para executar a verificação completa do reactor, incluindo a fase `verify`:

```bash
mvn verify
```

O GitHub Actions executa `mvn verify` em pushes e pull requests, usando Java 17
e Chrome, e publica relatórios do Surefire/Allure como artefato da execução.
Os módulos Selenium precisam do Chrome instalado; o `viacep-module` precisa de
acesso à internet para consultar a API pública.

O adaptador Allure do `viacep-module` grava resultados em
`viacep-module/target/allure-results`. Para gerar o relatório HTML, use a CLI
do Allure:

```bash
mvn -pl viacep-module test
allure generate viacep-module/target/allure-results \
  --output viacep-module/target/allure-report --clean
```
