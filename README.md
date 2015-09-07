# Kiiiosk open API

Конфиг тут: ./src/config.clj

## Usage

Убедитесь что:

```
> lein version
Leiningen 2.5.2 on Java 1.7.0_76 Java HotSpot(TM) 64-Bit Server VM
```

Пример как разворачивать для production: http://www.luminusweb.net/docs/deployment.md

### Run the application locally

`lein ring server`

### RabbitMQ workers
Запуск воркера генерации яндекс-каталога

`PGDATABASE=kiiiosk_development ~/bin/lein run -m workers.yandex-market`

Команда на генерацию яндекс каталога
$VENDOR_ID идентификатор вендора, для которого генерируем каталог

`PGDATABASE=kiiiosk_development ~/bin/lein run -m commands.yandex-market $VENDOR_ID`

### Run the application via Vagrant

`lein ring server-headless`

### Run the tests

`lein test`

### Production

На 2-м сервере:

`tail -f /var/log/tomcat7/catalina.out`

### Packaging and running as standalone jar

```
lein do clean, ring uberjar
java -jar target/server.jar
```

### Packaging as war

`lein ring uberwar`

### Deploy
`bundle exec cap production deploy `

## License

Copyright ©  FIXME
