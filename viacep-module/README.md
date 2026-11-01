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

Na raiz do repositório, execute todos os módulos:

```bash
mvn test
```

Para executar apenas um módulo:

```bash
mvn -pl viacep-module test
```

Os testes do ViaCEP chamam a API pública e precisam de acesso à internet. O
adaptador Allure grava resultados em
`viacep-module/target/allure-results`. Para gerar o relatório HTML, use a CLI
do Allure:

```bash
mvn -pl viacep-module test
allure generate viacep-module/target/allure-results \
  --output viacep-module/target/allure-report --clean
```
