# Nord Java Tests

Тестовый проект для API endpoint с авторизацией по токену.

## Стек

- Java 17
- Spring Boot 4.1.2
- JUnit 5
- Allure 3.0.0
- Lombok
- WireMock 3.13.2

## Структура тестов

- `EndpointLoginTest` - тесты авторизации (LOGIN)
- `EndpointLogoutTest` - тесты завершения сессии (LOGOUT)
- `EndpointActionTest` - тесты выполнения действий (ACTION)
- `EndpointE2ETest` - end-to-end сценарий

Теги:
- `smoke` - дымовые тесты
- `regress` - регрессионные тесты
- `positive` - позитивные сценарии
- `negative` - негативные сценарии
- `e2e` - end-to-end тесты

## Требования

Приложение должно быть запущено на порту **8080** (настраивается в `src/test/resources/application.yml`)

## WireMock моки

Для локального запуска используется WireMock:

```bash
cd wiremock
docker-compose up -d
```

WireMock поднимается на порту `8888:8080` и предоставляет моки:
- Успешные ответы (`rs-good.json`)
- Ошибки клиента: 400, 401, 403, 415
- Ошибки сервера: 500, 503, 504

Маппинги находятся в `wiremock/mappings/`

## Запуск тестов

```bash
mvn clean test
```

С указанием API ключа:
```bash
mvn clean test -Dapi.key=your_api_key
```

С указанием URL приложения:
```bash
mvn clean test -Dapp.url=http://localhost:8080 -Dapi.key=your_api_key
```

Запуск по тегам:
```bash
mvn clean test -Dgroups=regress
mvn clean test -Dgroups=smoke
mvn clean test -Dgroups=e2e
mvn clean test -Dgroups=positive
mvn clean test -Dgroups=negative
```

## Allure отчеты

```bash
mvn allure:report
allure serve target/allure-results
```

## Конфигурация

`src/test/resources/application.yml`:
```yaml
app:
  url: http://localhost:8080
  x-api-key: ${X_API_KEY:UNKNOWN}
```

API ключ передается через `-Dapi.key` или переменную окружения `X_API_KEY`.
