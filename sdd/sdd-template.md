# Описание дизайна программного обеспечения

Для **TypePulse** — веб-приложения (интерактивный тренажёр скорости печати)

Версия 0.2

Подготовила Лубочко Ульяна Антоновна

БГУИР, кафедра ЭВМ, группа 450501

2026-11-03

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
| ISO/IEC/IEEE 42010, IEEE 1016 | справочная | стандарты архитектуры и SDD |

### 1.5 Обзор документа {#15-document-overview}

Раздел 2 описывает стейкхолдеров и выбранные точки зрения. Раздел 3 содержит проектные представления, достаточные для реализации MVP. Раздел 4 ссылается на ADR. Раздел 5 — системные требования с приоритетами, оценки и согласованный план итераций. Изменения фиксируются в таблице «История изменений».

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
- **More Information:** REQ-FUNC-003, REQ-REL-001.

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
- **More Information:** VIEW-005.

### VIEW-008 — Patterns, deployment, resources

- **ID:** VIEW-008  
- **Title:** Стиль, развёртывание и ресурсы  
- **Viewpoint:** Patterns / Deployment / Resources  
- **Representation:** Layered modular monolith; один узел JVM + БД; локальный запуск через README (JDK 17+).  
- **More Information:** `adr/adr-template.md`; REQ-INST-001, REQ-PERF-002.

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

**Вне плана MVP:** SYS-F-09 Admin text catalog CRUD.
