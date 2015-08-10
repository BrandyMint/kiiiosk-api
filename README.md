# Kiiiosk open API

Конфиг тут: ./src/config.clj

## Usage

Пример как разворачивать для production: http://www.luminusweb.net/docs/deployment.md

### Run the application locally

`lein ring server`

### Run the tests

`lein test`

### Packaging and running as standalone jar

```
lein do clean, ring uberjar
java -jar target/server.jar
```

### Packaging as war

`lein ring uberwar`

## License

Copyright ©  FIXME
