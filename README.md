# Kiiiosk open API

Конфиг тут: ./src/config.clj

## Запуск локально
При первом запуске необходимо в корне создать файл `profiles.clj`, пример содержимого
для файла можно посмотреть в `profiles.clj.example`

Варианты запуск сервера
```
lein ring server                      ;; Запускается сервер, открывается браузер
lein ring server-headless             ;; Запускается сервер
lein with-profile dev server-headless ;; Запускается сервер с профилем dev
```

Варианты запуска repl
```
lein repl                  ;; Запускается repl
lein with-profile dev repl ;; Запускается repl с профилем dev
```

Пример как разворачивать для production: http://www.luminusweb.net/docs/deployment.md

### RabbitMQ workers

Генерация яндекс-каталога

Запуск:
`lein run -m workers.yandex-market`

Команда:
`lein run -m commands.yandex-market $VENDOR_ID`

Генерация мейл-каталога

Запуск:
`lein run -m workers.torg-mail`

Команда:
`lein run -m commands.torg-mail $VENDOR_ID`

### Run the tests

`lein test`

### Production

На 2-м сервере:

`tail -f /var/log/upstart/{yandex-market-worker,torg-mail-worker,openapi-web}.log`

### Packaging and running as standalone jar

```
lein do clean, ring uberjar
java -jar target/server.jar
```

### Packaging as war

`lein ring uberwar`

### Deploy production
`bundle exec cap production deploy `

### Deploy staging
`bundle exec cap staging deploy`

## License

Copyright ©  FIXME
