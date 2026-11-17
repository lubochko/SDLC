# TypePulse

Веб-тренажёр скорости печати (ЖЦРПО, MVP). Стек: Java 17, Spring Boot 3, Thymeleaf, Spring Security, H2.

## Итерация I1 (лаб. №5)

- SYS-F-01 — регистрация / вход / выход (BCrypt)
- SYS-F-03 — расчёт WPM / точности / ошибок на сервере
- SYS-F-02 (срез) — таймер, подсветка, live WPM
- История последних результатов (заготовка под I3)

## Запуск

Требования: JDK 17+, Maven 3.9+.

```bash
cd typepulse
mvn spring-boot:run
```

Открыть: http://localhost:8080

Тесты:

```bash
mvn test
```

## Документация

- SRS: `../srs/`
- SDD: `../sdd/sdd-template.md`
- ADR: `../adr/adr-template.md`
- UML (лаб. №5): `../uml/`
