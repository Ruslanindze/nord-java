# WireMock Server

Локальный mock-сервер для внешнего сервиса (/auth и /doAction).

## Структура

```
wiremock/
├── docker-compose.yml    # Конфигурация Docker Compose
├── mappings/             # Моки HTTP-запросов
│   ├── rs-err-400.json
│   ├── rs-err-401.json
│   ├── rs-err-403.json
│   ├── rs-err-415.json
│   ├── rs-err-500.json
│   ├── rs-err-503.json
│   ├── rs-err-504.json
│   └── rs-good.json
└── __files/              # Статические файлы (если нужны)
```

## Запуск

1. **Запустить WireMock:**
   ```bash
   cd wiremock
   docker-compose up -d
   ```
2. **Проверить работу:**
   ```bash
   curl --request POST \
   --url http://localhost:8888/auth \
   --header 'Content-Type: application/x-www-form-urlencoded' \
   --data token=954C16255813460AA1A67F33B610GOOD
   ```

3. **Остановить WireMock:**
   ```bash
   docker-compose down
   ```

## Доступные моки

Реализованы моки на поинты /auth и /doAction.
Пример запроса, где будет ответ с ошибкой:
curl --request POST \
--url http://localhost:8888/auth \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data token=954C16255813460AA1A67F33B6ERR400

* Чтобы получить другую ошибку нужно изменить конец токена ERR*** (подставить код ошибки, в структуре описано какие есть
  ошибки)
* Чтобы получить позитивный ответ для /auth или /doAction нужно отправить запрос с любым другим токеном.

## Добавление новых моков

1. Создайте JSON-файл в папке `mappings/`
2. Следуйте формату WireMock:
   ```json
   {
     "request": {
       "method": "GET",
       "urlPathPattern": "/objects/1"
     },
     "response": {
       "status": 200,
       "jsonBody": { ... },
       "headers": {
         "Content-Type": "application/json"
       }
     }
   }
   ```
3. Перезапустите WireMock: `docker-compose restart`
