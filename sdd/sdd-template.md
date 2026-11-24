# Описание дизайна программного обеспечения

Для **TypePulse** — веб-приложения (интерактивный тренажёр скорости печати)

Версия 0.4

Подготовила Лубочко Ульяна Антоновна

БГУИР, кафедра ЭВМ, группа 450501

2026-11-24

## Оглавление

<!-- TOC -->
* [1. Введение](#1-introduction)
  * [1.1 Назначение документа](#11-document-purpose)
  * [1.2 Область применения](#12-subject-scope)
  * [1.3 Определения, акронимы и сокращения](#13-definitions-acronyms-and-abbreviations)
  * [1.4 Ссылки](#14-references)
  * [1.5 Обзор документа](#15-document-overview)
* [2. Обзор дизайна](#2-design-overview)
  * [2.1 Задачи стейкхолдеров](#21-stakeholder-concerns)
  * [2.2 Выбранные точки зрения](#22-selected-viewpoints)
    * [2.2.1 Контекст](#221-context)
    * [2.2.2 Композиция](#222-composition)
    * [2.2.3 Логическое представление](#223-logical)
    * [2.2.4 Физическое представление](#224-physical)
    * [2.2.5 Структура](#225-structure)
    * [2.2.6 Зависимости](#226-dependency)
    * [2.2.7 Информация](#227-information)
    * [2.2.8 Интерфейс](#228-interface)
    * [2.2.9 Взаимодействие](#229-interaction)
    * [2.2.10 Алгоритм](#2210-algorithm)
    * [2.2.11 Динамика состояний](#2211-state-dynamics)
    * [2.2.12 Параллелизм](#2212-concurrency)
    * [2.2.13 Паттерны](#2213-patterns)
    * [2.2.14 Развёртывание](#2214-deployment)
    * [2.2.15 Ресурсы](#2215-resources)
* [3. Проектные представления](#3-design-views)
* [4. Решения](#4-decisions)
* [5. Приложения](#5-appendixes)
<!-- TOC -->

## История изменений

| Name | Date | Reason For Changes | Version |
|------|------|--------------------|---------|
| Лубочко У. А. | 2026-11-03 | Первичное проектирование TypePulse по согласованному SRS (лаб. №4) | 0.1 |
| Лубочко У. А. | 2026-11-03 | Уточнение приоритетов, оценок и плана итераций после согласования с Заказчиком | 0.2 |
| Лубочко У. А. | 2026-11-17 | UML динамического поведения (use case / activity / state / sequence), ссылки на артефакты лаб. №5 | 0.3 |
| Лубочко У. А. | 2026-11-24 | UML статической структуры (классы, компоненты, развёртывание) — лаб. №6; словесные описания представлений | 0.4 |

---

## 1. Введение {#1-introduction}

### 1.1 Назначение документа {#11-document-purpose}

Документ SDD описывает **как** будет построено веб-приложение TypePulse. Он служит руководством к действию для разработчиков и фиксирует дизайн для архитектора и Заказчика. Связанные документы: согласованный SRS (лаб. №3, мокапы и SysML в `srs/`), ADR в `adr/adr-template.md`.

### 1.2 Область применения {#12-subject-scope}

**Система:** TypePulse, релиз MVP.

**Назначение:** оценка и улучшение навыков набора текста (WPM, точность, история, рейтинг).

**Входит в дизайн:** Web UI тренировки, аутентификация, расчёт метрик, сохранение результатов, история, рейтинг, пользовательские тексты, односерверное развёртывание на Java (Spring Boot).

**Не входит:** native mobile, ИИ-подбор сложности, realtime-дуэли, OAuth, интеграция с LMS, Admin CRUD каталога текстов (отложено), географически распределённый деплой.

### 1.3 Определения, акронимы и сокращения {#13-definitions-acronyms-and-abbreviations}

| Term | Definition |
|------|------------|
| ADR | Architecture Decision Record |
| API | Application Programming Interface |
| MVP | Minimum Viable Product |
| pd | Person-day — человеко-день оценки трудозатрат |
| REST | Representational State Transfer |
| SDD | Software Design Description |
| SRS | Software Requirements Specification |
| UI | User Interface |
| WPM | Words Per Minute — скорость набора |

### 1.4 Ссылки {#14-references}

| Название | Тип | Расположение |
|----------|-----|--------------|
| SRS / мокапы GUI / SysML TypePulse | нормативная | `srs/mockups/`, `srs/sysml/` |
| ADR: Spring Boot-монолит | нормативная | `adr/adr-template.md` |
| UML динамического поведения (лаб. №5) | нормативная | `uml/diagrams/` (PDF) |
| UML статической структуры (лаб. №6) | нормативная | VIEW-010…012 настоящего SDD; ADR |
| ISO/IEC/IEEE 42010, IEEE 1016 | справочная | стандарты архитектуры и SDD |

### 1.5 Обзор документа {#15-document-overview}

Раздел 2 описывает стейкхолдеров и выбранные точки зрения. Раздел 3 содержит проектные представления, достаточные для реализации MVP, включая UML динамики (лаб. №5) и статической структуры (лаб. №6: VIEW-010…012). Раздел 4 ссылается на ADR. Раздел 5 — системные требования с приоритетами, оценки и план итераций. Изменения фиксируются в таблице «История изменений».

---

## 2. Обзор дизайна {#2-design-overview}

### 2.1 Задачи стейкхолдеров {#21-stakeholder-concerns}

| Стейкхолдер | Задачи | Адресующие viewpoints / views |
|-------------|--------|-------------------------------|
| Пользователь | Быстрая обратная связь при вводе, понятные метрики | Context, Interaction, Algorithm, State Dynamics |
| Заказчик | Срок, стоимость, приоритизация MVP | Composition, Deployment, Resources; Приложение A–C |
| Разработчик | Понятные слои, тестируемая формула WPM | Logical, Structure, Dependency, Interface |
| Сопровождение | Простой деплой, логи без секретов | Deployment, Resources |

### 2.2 Выбранные точки зрения {#22-selected-viewpoints}

Для MVP выбраны: Context, Composition, Logical, Structure, Dependency, Information, Interface, Interaction, Algorithm, State Dynamics, Patterns, Deployment, Resources.  
Physical и Concurrency для MVP не детализируются отдельно (кратко учтены в Deployment / Resources).

#### 2.2.1 Контекст {#221-context}

Система как чёрный ящик: пользователь в браузере ↔ TypePulse. Внешние SaaS в MVP отсутствуют. Представление: VIEW-001.

#### 2.2.2 Композиция {#222-composition}

Монолит Spring Boot из модулей ui, auth, training, metrics, results, ranking, texts. Представление: VIEW-002.

#### 2.2.3 Логическое представление {#223-logical}

Сущности User, TextSnippet, TrainingSession, Result. Представление: VIEW-003.

#### 2.2.4 Физическое представление {#224-physical}

Не детализируется: учебный/локальный хост без облачной топологии. См. VIEW-008 (Deployment).

#### 2.2.5 Структура {#225-structure}

Внутренняя организация модулей и слоёв Controller → Service → Repository. См. VIEW-002, VIEW-008.

#### 2.2.6 Зависимости {#226-dependency}

UI → auth/training/results; training → metrics/texts; results → ranking. Направление зависимостей одностороннее к данным. См. VIEW-002.

#### 2.2.7 Информация {#227-information}

Реляционное хранение (PostgreSQL; H2 для local-профиля). См. VIEW-003.

#### 2.2.8 Интерфейс {#228-interface}

MVC/страницы и endpoints тренировки, истории, рейтинга. См. VIEW-004.

#### 2.2.9 Взаимодействие {#229-interaction}

Сценарий тренировки (start → input → finish → result). См. VIEW-005.

#### 2.2.10 Алгоритм {#2210-algorithm}

Расчёт WPM и точности на сервере. См. VIEW-006.

#### 2.2.11 Динамика состояний {#2211-state-dynamics}

Состояния сеанса CREATED / RUNNING / FINISHED / CANCELLED. См. VIEW-007.

#### 2.2.12 Параллелизм {#2212-concurrency}

Отдельное представление не вводится: запросы обрабатываются моделью один запрос — один поток сервлет-контейнера; критичные записи результата идемпотентны по sessionId.

#### 2.2.13 Паттерны {#2213-patterns}

Layered architecture, modular monolith. См. VIEW-008 и `adr/adr-template.md`.

#### 2.2.14 Развёртывание {#2214-deployment}

Один JVM-процесс + БД. См. VIEW-008.

#### 2.2.15 Ресурсы {#2215-resources}

Ориентир для демо: 1 CPU, ≥1 GB RAM; отклик подсветки ввода ≤100 мс локально (REQ-PERF-001). См. VIEW-008.

---

## 3. Проектные представления {#3-design-views}

### VIEW-001 — System context

- **ID:** VIEW-001  
- **Title:** Контекст TypePulse  
- **Viewpoint:** Context  
- **Representation:**

```
[User] --HTTPS--> [Browser] --> [TypePulse App]
                                    |
                              [PostgreSQL / H2]
```

- **More Information:** мокапы `srs/mockups/`; REQ-INT-*; ADR `adr/adr-template.md`.

### VIEW-002 — Component composition

- **ID:** VIEW-002  
- **Title:** Компоненты монолита  
- **Viewpoint:** Composition / Structure / Dependency  
- **Representation:**

| Component | Responsibility |
|-----------|----------------|
| `ui` | Экраны Home, Setup, Training, Result, History, Leaderboard, Custom Texts |
| `auth` | Регистрация, вход, выход, session |
| `training` | Сеанс тренировки, таймер, приём ввода |
| `metrics` | WPM, accuracy, errors |
| `results` | Сохранение и история |
| `ranking` | Топ-20 по лучшему WPM |
| `texts` | Каталог и пользовательские тексты |

- **More Information:** REQ-FUNC-001…007; SYS-F-01…08 (Приложение A).

### VIEW-003 — Logical / information model

- **ID:** VIEW-003  
- **Title:** Логическая модель данных  
- **Viewpoint:** Logical / Information  
- **Representation:**

```
User (id, login, email, passwordHash, role)
TextSnippet (id, language, difficulty, body, ownerUserId?)
TrainingSession (id, userId, textId, durationSec, startedAt, status)
Result (id, sessionId, userId, wpm, accuracy, errors, finishedAt)
```

- **More Information:** REQ-FUNC-005…007; SYS-F-05, SYS-F-08.

### VIEW-004 — Interfaces

- **ID:** VIEW-004  
- **Title:** Интерфейсы приложения  
- **Viewpoint:** Interface  
- **Representation:**

| Page / Endpoint | Method | Назначение |
|-----------------|--------|------------|
| `/register`, `/login`, `/logout` | GET/POST | Auth |
| `/training/setup` | GET/POST | Параметры тренировки |
| `/training/start` | POST | Старт сеанса |
| `/training/{id}/finish` | POST | Завершение и метрики |
| `/history` | GET | История |
| `/leaderboard` | GET | Рейтинг |
| `/texts/custom` | GET/POST | Пользовательские тексты |

Сервер — источник истины для финальных метрик.  
- **More Information:** REQ-INT-004, REQ-FUNC-004.

### VIEW-005 — Training interaction

- **ID:** VIEW-005  
- **Title:** Сценарий тренировки  
- **Viewpoint:** Interaction  
- **Representation:** User выбирает параметры → создаётся RUNNING-сеанс → UI принимает ввод → по таймеру/концу текста вызывается finish → сохраняется Result → экран результата. При обрыве сети сеанс CANCELLED, успешный результат не пишется.  
- **UML:** диаграмма последовательности `uml/diagrams/sequence.pdf`; диаграмма активности `uml/diagrams/activity.pdf`.  
- **More Information:** REQ-FUNC-003, REQ-REL-001; `uml/glossary.md`, `uml/additional-specs.md`.

### VIEW-006 — Metrics algorithm

- **ID:** VIEW-006  
- **Title:** Расчёт WPM и точности  
- **Viewpoint:** Algorithm  
- **Representation:**

```
WPM       = (correctChars / 5) / (elapsedMs / 60000)
accuracy% = (correctChars / max(typedChars, 1)) * 100
errors    = число ошибочных символов
```

- **More Information:** REQ-FUNC-004, REQ-PERF-001.

### VIEW-007 — Session state dynamics

- **ID:** VIEW-007  
- **Title:** Состояния тренировки  
- **Viewpoint:** State Dynamics  
- **Representation:** `CREATED → RUNNING → FINISHED | CANCELLED`. FINISHED порождает неизменяемый Result.  
- **UML:** `uml/diagrams/state.pdf`.  
- **More Information:** VIEW-005.

### VIEW-009 — Use case model (лаб. №5)

- **ID:** VIEW-009  
- **Title:** Модель вариантов использования TypePulse  
- **Viewpoint:** Interaction / Context  
- **Representation:** актёр Пользователь (и при необходимости Таймер); прецеденты регистрации, входа, настройки и проведения тренировки, расчёта/сохранения результата, истории, рейтинга, загрузки текста, выхода; связи `<<include>>` / `<<extend>>`.  
- **UML:** `uml/diagrams/use-case.pdf`; глоссарий — `uml/glossary.md`; доп. спецификации — `uml/additional-specs.md`.  
- **More Information:** REQ-FUNC-001…007; SYS-F-01…08.

### VIEW-008 — Patterns, deployment, resources

- **ID:** VIEW-008  
- **Title:** Стиль, развёртывание и ресурсы  
- **Viewpoint:** Patterns / Deployment / Resources  
- **Representation:** Layered modular monolith; один узел JVM + БД; локальный запуск через README (JDK 17+). Детализация узлов и артефактов — VIEW-012.  
- **More Information:** `adr/adr-template.md`; REQ-INST-001, REQ-PERF-002.

### VIEW-010 — Class diagram (лаб. №6)

- **ID:** VIEW-010  
- **Title:** Диаграмма классов TypePulse  
- **Viewpoint:** Logical / Structure  
- **Representation (словесно):**  
  На диаграмме классов показаны сущности доменной модели и сервисный слой MVP.  
  **Классы-сущности:** `UserAccount` (id, login, email, passwordHash, role), `TextSnippet` (id, language, difficulty, body, ownerUserId), `TrainingSession` (id, userId, textId, durationSec, startedAt, status), `Result` (id, sessionId, userId, wpm, accuracy, errors, elapsedMs, finishedAt).  
  **Классы-сервисы:** `AuthService` (register, authenticate), `TrainingService` (startSession, finishSession, cancelSession), `MetricsService` (calculate — формула WPM/accuracy/errors).  
  **Связи:**  
  - `UserAccount` «1» — «*» `TrainingSession` (ассоциация: пользователь проводит много сеансов);  
  - `UserAccount` «1» — «*» `Result` (история результатов);  
  - `TextSnippet` «1» — «*» `TrainingSession` (текст используется в сеансах);  
  - `TrainingSession` «1» — «0..1» `Result` (успешный сеанс порождает один результат; при CANCELLED результата нет);  
  - `TrainingService` зависит от `MetricsService` (пунктир dependency: finish вызывает calculate);  
  - `AuthService` / `TrainingService` зависят от соответствующих сущностей через репозитории (на диаграмме допустимо показать dependency на `UserAccount`, `TrainingSession`, `Result`).  
  Видимость атрибутов: поля сущностей — private (`-`), операции сервисов — public (`+`).  
- **More Information:** VIEW-003; REQ-FUNC-001…005; ADR раздел «статическая структура».

### VIEW-011 — Component diagram (лаб. №6)

- **ID:** VIEW-011  
- **Title:** Диаграмма компонентов TypePulse  
- **Viewpoint:** Composition / Dependency  
- **Representation (словесно):**  
  Компоненты модульного монолита (логические модули одного JAR):  
  - `ui` — веб-страницы (Thymeleaf) и клиентский JS подсветки;  
  - `auth` — регистрация, вход, выход, сессии;  
  - `training` — настройка и проведение сеанса;  
  - `metrics` — расчёт WPM/точности;  
  - `results` — сохранение Result и история;  
  - `ranking` — топ-20;  
  - `texts` — каталог и пользовательские тексты;  
  - артефакт данных `database` (H2/PostgreSQL) как внешний компонент хранения.  
  **Интерфейсы (предоставляемые / требуемые):**  
  - `auth` предоставляет `IAuthentication`; `ui` и `training` его требуют;  
  - `training` предоставляет `ITraining`; `ui` требует;  
  - `metrics` предоставляет `IMetrics`; `training` требует;  
  - `results` предоставляет `IResults`; `ui` и `ranking` требуют;  
  - `texts` предоставляет `ITexts`; `training` требует;  
  - `results` / `texts` / `auth` требуют интерфейс доступа к БД (`IPersistence` / JDBC).  
  Зависимости направлены от UI к доменным компонентам и от доменных — к данным (без циклов UI↔БД напрямую для бизнес-правил).  
- **More Information:** VIEW-002; ADR-001.

### VIEW-012 — Deployment diagram (лаб. №6)

- **ID:** VIEW-012  
- **Title:** Диаграмма развёртывания TypePulse  
- **Viewpoint:** Deployment / Physical  
- **Representation (словесно):**  
  Три узла учебного/демо-развёртывания:  
  1. **ClientPC** (`<<device>>`) — браузер пользователя; размещён артефакт клиентской части (HTML/CSS/JS страниц TypePulse).  
  2. **AppServer** (`<<device>>` / execution environment JVM) — исполняемый артефакт `typepulse.jar` (Spring Boot modular monolith), внутри которого работают компоненты `ui`, `auth`, `training`, `metrics`, `results`, `ranking`, `texts`.  
  3. **DbServer** (`<<device>>`) — СУБД H2 (локальный профиль) или PostgreSQL; артефакт схемы данных TypePulse.  
  **Соединения:** ClientPC — AppServer по **HTTP/HTTPS**; AppServer — DbServer по **JDBC**.  
  Геораспределение и балансировщики в MVP отсутствуют (односерверная схема, REQ-INST-001).  
- **More Information:** VIEW-008; ADR-001.

---

## 4. Решения {#4-decisions}

- **ID:** ADR-001  
- **Title:** Выбор Spring Boot-монолита для веб-приложения TypePulse  
- **Context:** Нужен архитектурный стиль Java Web MVP в срок трёх итераций  
- **Options:** Spring Boot modular monolith; микросервисы; Jakarta EE без Spring  
- **Outcome:** выбран Spring Boot modular monolith — срок, Java и простота демо  
- **More Information:** полный протокол по шаблону MADR — `adr/adr-template.md`

---

## 5. Приложения {#5-appendixes}

### A. Системные требования (приоритет 10 = highest … 50 = lowest)

| ID | Feature (EN) | Priority | Type | Trace (SRS) | Est. (pd) |
|----|--------------|----------|------|-------------|-----------|
| SYS-F-01 | User registration and login | 10 | Functional | REQ-FUNC-001, REQ-SEC-001 | 5 |
| SYS-F-02 | Training session (text, highlight, timer) | 10 | Functional | REQ-FUNC-003, REQ-INT-002 | 8 |
| SYS-F-03 | Metrics calculation (WPM, accuracy, errors) | 10 | Functional | REQ-FUNC-004 | 3 |
| SYS-F-04 | Training setup (language, duration, difficulty) | 15 | Functional | REQ-FUNC-002 | 2 |
| SYS-F-05 | Persist training result | 20 | Functional | REQ-FUNC-005 | 3 |
| SYS-F-06 | Personal history view | 25 | Functional | REQ-FUNC-006 | 2 |
| SYS-F-07 | Leaderboard top-20 | 30 | Functional | REQ-FUNC-007 | 3 |
| SYS-F-08 | Custom user texts upload | 20 | Functional | (согласовано с Заказчиком) | 4 |
| SYS-F-09 | Admin text catalog CRUD | 50 | Functional | REQ-FUNC-008 | 4 (deferred) |
| SYS-NF-01 | Keystroke feedback ≤ 100 ms (local) | 10 | Non-functional | REQ-PERF-001 | 1 |
| SYS-NF-02 | Password hashing | 10 | Non-functional | REQ-SEC-001 | 0.5 |
| SYS-NF-03 | History page ≤ 2 s (≤10k rows) | 35 | Non-functional | REQ-PERF-002 | 1 |
| SYS-NF-04 | Local run via README (JDK 17+) | 20 | Non-functional | REQ-INST-001 | 1 |
| SYS-NF-05 | Error logging without secrets | 40 | Non-functional | REQ-OBS-001 | 0.5 |

### B. Оценки трудозатрат (обоснование)

| Feature | Почему оценка такая | Возможность сократить |
|---------|---------------------|------------------------|
| SYS-F-02 | Таймер, подсветка, обрыв сети, согласование с сервером | Убрать live-WPM (−1…2 pd) — отклонено Заказчиком |
| SYS-F-01 | Хеш паролей, сессии, валидация | Гостевой режим ломает историю/рейтинг |
| SYS-F-08 | Валидация длины/языка, выбор в Setup | Лимит ≤2000 символов, plain text |
| SYS-F-09 | Полный CRUD | Исключено из MVP (priority 50) |

Длина итерации: **10 рабочих дней**. Функции длиннее итерации режутся на вертикальные срезы (см. план C).

### C. Согласованный план реализации по итерациям

**Команда:** 1 Regular + 1 Junior (Junior с итерации 2).  
**Дедлайн продукта:** конец итерации 3 (без сдвига).

| Iteration | Regular (pd) | Junior (pd) | Scope |
|-----------|--------------|-------------|-------|
| I1 | 10 | 0 | SYS-F-01; SYS-F-03; вертикальный срез SYS-F-02 (timer + highlight + live WPM skeleton) |
| I2 | 8 | 4 | Завершение SYS-F-02; SYS-F-04; SYS-F-05; SYS-F-08 (Junior: UI формы) |
| I3 | 7 | 5 | SYS-F-06 (Junior UI); SYS-F-07; SYS-NF-03…05; интеграция и демо-буфер |

**Статус (лаб. №5–6):** динамическое поведение задокументировано UML (PDF в `uml/diagrams/`); статическая структура описана в VIEW-010…012. Разработка основного функционала продолжается по плану I1–I3 (ADR / приложение C).

**Вне плана MVP:** SYS-F-09 Admin text catalog CRUD.

### D. UML-артефакты динамического поведения (лаб. №5)

| Диаграмма | Файл |
|-----------|------|
| Use Case | `uml/diagrams/use-case.pdf` |
| Activity | `uml/diagrams/activity.pdf` |
| State | `uml/diagrams/state.pdf` |
| Sequence | `uml/diagrams/sequence.pdf` |

### E. UML-артефакты статической структуры (лаб. №6)

| Диаграмма | Где описана в SDD |
|-----------|-------------------|
| Классов | VIEW-010 |
| Компонентов | VIEW-011 |
| Развёртывания | VIEW-012 |

Механизмы построения и трассировка к монолиту — в `adr/adr-template.md` (раздел лаб. №6).
