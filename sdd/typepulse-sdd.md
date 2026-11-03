# Описание дизайна программного обеспечения

Для **TypePulse** — веб-приложения (интерактивный тренажёр скорости печати)

Версия 0.3

Подготовил(а) Лубочко Ульяна Антоновна (Исполнитель / Архитектор)

БГУИР, кафедра ЭВМ, группа 450501

2026-11-03

## Оглавление

<!-- TOC -->
* [1. Введение](#1-introduction)
* [2. Обзор дизайна](#2-design-overview)
* [3. Проектные представления](#3-design-views)
* [4. Решения](#4-decisions)
* [5. Приложения](#5-appendixes)
  * [A. Системные требования и приоритеты](#a-system-requirements)
  * [B. Оценки трудозатрат](#b-effort)
  * [C. Переписка с Заказчиком (раунд 1)](#c-round1)
  * [D. План итераций v1 (после раунда 1)](#d-plan-v1)
  * [E. Переписка с Заказчиком (раунд 2)](#e-round2)
  * [F. План итераций v2 (согласованный)](#f-plan-v2)
  * [G. Сводка стоимости](#g-cost)
  * [H. Контрольные вопросы](#h-control)
<!-- TOC -->

## История изменений

| Name | Date | Reason For Changes | Version |
|------|------|--------------------|---------|
| Лубочко У. А. | 2026-11-03 | Первичное проектирование по SRS TypePulse (лаб. №4) | 0.1 |
| Лубочко У. А. | 2026-11-03 | Корректировка приоритетов и план итераций после раунда 1 с Заказчиком | 0.2 |
| Лубочко У. А. | 2026-11-03 | Новый функционал (custom texts), junior, план v2 и стоимость после раунда 2 | 0.3 |

---

## 1. Введение {#1-introduction}

### 1.1 Назначение документа {#11-document-purpose}

SDD описывает **как** будет построено веб-приложение TypePulse на основе согласованного SRS (`srs/`, мокапы и SysML из лаб. №3). Документ предназначен для разработчиков (руководство к реализации), архитектора и Заказчика (согласование границ дизайна, сроков и стоимости). Связанные артефакты: SRS TypePulse, ADR в каталоге `adr/`.

### 1.2 Область применения {#12-subject-scope}

**Система:** TypePulse v1 (MVP) + согласованные дополнения после обсуждения с Заказчиком.

**Входит:** Web UI тренировки, аутентификация, расчёт WPM/точности, сохранение результатов, история, рейтинг, загрузка пользовательских текстов (добавлено в раунде 2), развёртывание на одном сервере/локально.

**Не входит:** native mobile, ИИ-подбор сложности, realtime-дуэли, OAuth, LMS-интеграции, полноценный SLA 24/7.

### 1.3 Определения, акронимы и сокращения {#13-definitions-acronyms-and-abbreviations}

| Term | Definition |
|------|------------|
| ADR | Architecture Decision Record |
| JWT | JSON Web Token (не используется в MVP; см. ADR-0003) |
| REST | Representational State Transfer |
| SDD | Software Design Description |
| SRS | Software Requirements Specification |
| WPM | Words Per Minute |
| Person-day (pd) | Человеко-день (~6–8 ч чистой разработки) |
| Iteration | Итерация разработки фиксированной длины (10 рабочих дней) |

### 1.4 Ссылки {#14-references}

| Название | Тип | Расположение |
|----------|-----|--------------|
| SRS TypePulse / мокапы / SysML | нормативная | `srs/mockups/`, `srs/sysml/` |
| Шаблон SDD | учебная | `sdd/sdd-template.md` |
| ADR-0001…0003 | нормативная | `adr/0001-*.md` … |
| IEEE 1016 / ISO 42010 | справочная | стандарты дизайна |

### 1.5 Обзор документа {#15-document-overview}

Раздел 2 фиксирует стейкхолдеров и выбранные viewpoints. Раздел 3 — проектные представления (достаточно для реализации MVP). Раздел 4 ссылается на ADR. Раздел 5 содержит системные требования с приоритетами, переписку с Заказчиком, планы итераций v1/v2 и ответы на контрольные вопросы.

---

## 2. Обзор дизайна {#2-design-overview}

### 2.1 Задачи стейкхолдеров {#21-stakeholder-concerns}

| Стейкхолдер | Задачи (concerns) | Адресующие viewpoints |
|-------------|-------------------|------------------------|
| Конечный пользователь | Простая тренировка, мгновенная обратная связь по вводу, понятные метрики | Context, Interaction, Algorithm, State Dynamics |
| Заказчик | Срок, стоимость, приоритизация MVP | Composition, Deployment, Resources + Приложения C–G |
| Разработчик | Понятная структура слоёв, тестируемость формулы WPM | Logical, Structure, Dependency, Interface |
| Сопровождение | Простой деплой, логи ошибок | Deployment, Resources, Observability via Interface |

### 2.2 Выбранные точки зрения {#22-selected-viewpoints}

Для учебного MVP выбраны viewpoints: **Context, Composition, Logical, Information, Interface, Interaction, Algorithm, State Dynamics, Patterns, Deployment, Resources**.  
Не детализируются в v1: Physical (облачная топология), Concurrency (отдельный viewpoint) — кратко учтены в Deployment/Resources.

#### 2.2.1 Контекст {#221-context}
Система — чёрный ящик: браузер пользователя ↔ TypePulse. Внешних SaaS в MVP нет.

#### 2.2.2 Композиция {#222-composition}
Монолит Spring Boot: модули Web UI, Auth, Training, Results, Ranking, Texts.

#### 2.2.3 Логическое представление {#223-logical}
Классы/сущности: User, TrainingSession, Result, TextSnippet, LeaderboardEntry.

#### 2.2.7 Информация {#227-information}
Реляционная БД (PostgreSQL в целевой среде, H2 — для локального демо).

#### 2.2.8–2.2.11 Интерфейс, взаимодействие, алгоритм, состояния
REST/MVC endpoints + сценарий тренировки; формула WPM; конечный автомат сеанса.

#### 2.2.13–2.2.15 Паттерны, развёртывание, ресурсы
Layered architecture, один JVM-процесс, ограниченные ресурсы учебной среды.

---

## 3. Проектные представления {#3-design-views}

### VIEW-001 — System context

- **Title:** Контекст TypePulse  
- **Viewpoint:** Context  
- **Representation:**

```
[User] --HTTPS--> [Browser]
                     |
                     v
              [TypePulse App]
               /          \
         [Web UI]      [REST/MVC API]
                           |
                      [PostgreSQL/H2]
```

Акторы: User (студент/офисный пользователь), Admin (управление текстами — низкий приоритет).  
- **More Information:** REQ-INT-*, ADR-0001; мокапы `srs/mockups/`.

### VIEW-002 — Component composition

- **Title:** Компоненты монолита  
- **Viewpoint:** Composition / Structure  
- **Representation:**

| Component | Responsibility |
|-----------|----------------|
| `ui` | HTML/JS экраны: Home, Setup, Training, Result, History, Leaderboard, Custom Texts |
| `auth` | Регистрация, вход, сессия (cookie-session) |
| `training` | Выдача текста, приём ввода, завершение сеанса |
| `metrics` | Расчёт WPM, accuracy, errors |
| `results` | Persistence результатов, история |
| `ranking` | Топ-20 по лучшему WPM |
| `texts` | Каталог текстов + пользовательские тексты (раунд 2) |

- **More Information:** ADR-0001; REQ-FUNC-001…007.

### VIEW-003 — Logical model (core entities)

- **Title:** Логическая модель данных  
- **Viewpoint:** Logical / Information  
- **Representation:**

```
User (id, login, email, passwordHash, role)
TextSnippet (id, language, difficulty, body, ownerUserId?)
TrainingSession (id, userId, textId, durationSec, startedAt, status)
Result (id, sessionId, userId, wpm, accuracy, errors, finishedAt)
```

Связи: User 1—* Result; TextSnippet 1—* TrainingSession; Session 1—1 Result.  
- **More Information:** ADR-0002; REQ-FUNC-005…007.

### VIEW-004 — External / internal interfaces

- **Title:** Интерфейсы приложения  
- **Viewpoint:** Interface  
- **Representation (MVP):**

| Endpoint / Page | Method | Назначение |
|-----------------|--------|------------|
| `/register`, `/login`, `/logout` | POST/GET | Auth |
| `/training/setup` | GET/POST | Параметры тренировки |
| `/training/start` | POST | Старт сеанса |
| `/training/{id}/finish` | POST | Завершение + метрики |
| `/history` | GET | История пользователя |
| `/leaderboard` | GET | Рейтинг |
| `/texts/custom` | GET/POST | Пользовательские тексты (после раунда 2) |

Контракт finish: JSON/form `{ typedChars, correctChars, errors, elapsedMs }` → сервер пересчитывает WPM (источник истины — сервер).  
- **More Information:** REQ-INT-004, REQ-FUNC-004; ADR-0003.

### VIEW-005 — Training happy path

- **Title:** Сценарий тренировки  
- **Viewpoint:** Interaction  
- **Representation (последовательность):**

1. User выбирает language/duration/difficulty (или custom text).  
2. `training` создаёт `TrainingSession(status=RUNNING)`.  
3. UI показывает текст; JS фиксирует нажатия и подсветку.  
4. По таймеру/концу текста UI шлёт `finish`.  
5. `metrics` считает WPM/accuracy; `results` сохраняет; UI → Result screen.

Путь отказа: потеря сети → сеанс `CANCELLED`, результат не пишется как успешный (REQ-REL-001).

### VIEW-006 — WPM algorithm

- **Title:** Расчёт метрик  
- **Viewpoint:** Algorithm  
- **Representation:**

```
correctChars = число верно введённых символов
elapsedMin   = elapsedMs / 60000
WPM          = (correctChars / 5) / elapsedMin
accuracy%    = (correctChars / max(typedChars,1)) * 100
errors       = число ошибочных символов (по правилам SRS)
```

Инвариант: сервер пересчитывает метрики; клиентские значения — только для live-UI.  
- **More Information:** REQ-FUNC-004, REQ-PERF-001.

### VIEW-007 — Session state machine

- **Title:** Состояния тренировки  
- **Viewpoint:** State Dynamics  
- **Representation:**

```
CREATED --> RUNNING --> FINISHED
                \--> CANCELLED
FINISHED --> (immutable Result)
```

Триггеры: start, timer_elapsed | text_exhausted, network_loss | user_abort.

### VIEW-008 — Patterns & deployment

- **Title:** Стиль и развёртывание  
- **Viewpoint:** Patterns / Deployment / Resources  
- **Representation:** Layered monolith (Controller → Service → Repository). Один узел: JVM + БД. Локально: `./mvnw spring-boot:run`. Ресурсы: 1 CPU / 1 GB RAM достаточно для демо-группы.  
- **More Information:** ADR-0001, ADR-0002; REQ-INST-001, REQ-PERF-002.

---

## 4. Решения {#4-decisions}

Значимые решения вынесены в MADR:

| ID | Title | File |
|----|-------|------|
| ADR-0001 | Spring Boot monolith for TypePulse | `adr/0001-use-spring-boot-monolith.md` |
| ADR-0002 | Relational DB (PostgreSQL / H2) | `adr/0002-use-relational-database.md` |
| ADR-0003 | Server-rendered UI + cookie sessions | `adr/0003-server-rendered-ui-and-session-auth.md` |

Кратко:

- **ADR-0001 Outcome:** Spring Boot monolith — потому что учебный срок, одна команда, Java в SRS.  
- **ADR-0002 Outcome:** PostgreSQL (+ H2 profile) — потому что история/рейтинг требуют надёжной модели и простых запросов.  
- **ADR-0003 Outcome:** Thymeleaf + Spring Security form-login — потому что быстрее MVP без отдельного SPA-билда.

---

## 5. Приложения {#5-appendixes}

<a id="a-system-requirements"></a>
### A. Системные требования и приоритеты

Приоритет: **10 = highest … 50 = lowest** (английские имена функций).  
Статусы после согласования с Заказчиком отмечены в колонке Notes.

| ID | Feature (EN) | Priority | Type | Trace to SRS | Est. (pd) | Notes |
|----|--------------|----------|------|--------------|-----------|-------|
| SYS-F-01 | User registration and login | 10 | Functional | REQ-FUNC-001, REQ-SEC-001 | 5 | Must for history/ranking |
| SYS-F-02 | Training session (text + input highlight + timer) | 10 | Functional | REQ-FUNC-003, REQ-INT-002 | 8 | Core value |
| SYS-F-03 | Metrics calculation (WPM, accuracy, errors) | 10 | Functional | REQ-FUNC-004 | 3 | Server-side truth |
| SYS-F-04 | Training setup (language, duration, difficulty) | 15 | Functional | REQ-FUNC-002 | 2 | Raised from 20 after Round 1 |
| SYS-F-05 | Persist training result | 20 | Functional | REQ-FUNC-005 | 3 | |
| SYS-F-06 | Personal history view | 25 | Functional | REQ-FUNC-006 | 2 | Was 30; Customer raised |
| SYS-F-07 | Leaderboard top-20 | 30 | Functional | REQ-FUNC-007 | 3 | |
| SYS-F-08 | Custom user texts upload | 20 | Functional | new (Round 2) | 4 | Added Round 2; cut from admin CRUD |
| SYS-F-09 | Admin text catalog CRUD | 50 | Functional | REQ-FUNC-008 | 4 | Deferred / replaced by SYS-F-08 |
| SYS-NF-01 | Keystroke feedback ≤ 100 ms (local) | 10 | Non-functional | REQ-PERF-001 | 1 | Mostly client JS |
| SYS-NF-02 | Password hashing | 10 | Non-functional | REQ-SEC-001 | 0.5 | Incl. in SYS-F-01 |
| SYS-NF-03 | History page ≤ 2 s (≤10k rows) | 35 | Non-functional | REQ-PERF-002 | 1 | Index on userId+date |
| SYS-NF-04 | Local run via README (JDK 17+) | 20 | Non-functional | REQ-INST-001 | 1 | |
| SYS-NF-05 | Error logging without secrets | 40 | Non-functional | REQ-OBS-001 | 0.5 | |

<a id="b-effort"></a>
### B. Оценки трудозатрат (пояснение Заказчику)

Почему отдельные функции «длинные»:

| Feature | Why not faster | Option to shorten |
|---------|----------------|-------------------|
| SYS-F-02 Training session | Нужны таймер, подсветка, граничные случаи, согласование с сервером | Урезать live-WPM на клиенте; оставить метрики только на экране результата (−1…2 pd) |
| SYS-F-01 Auth | Хеширование, валидация, сессии, базовые тесты | Гостевой режим без аккаунта — но тогда нет истории/рейтинга (ломает цель продукта) |
| SYS-F-07 Leaderboard | Нужны агрегации и правила ничьих | Считать рейтинг только по последним 7 дням проще, но Заказчик хочет «лучший WPM» |
| SYS-F-08 Custom texts | Валидация длины/языка, хранение, выбор в Setup | Ограничить: plain text ≤ 2000 символов, без форматирования (−1 pd) |

Длина итерации: **10 рабочих дней**. Одна функция > длины итерации **не пихается целиком** — режется на вертикальные куски (см. планы и H1).

Ставка для учебной оценки стоимости (условно): **Junior 80 у.е./pd**, **Regular 120 у.е./pd**.

<a id="c-round1"></a>
### C. Переписка с Заказчиком — раунд 1

**Тема:** TypePulse — оценки и приоритеты MVP  
**Участники:** Исполнитель (Лубочко У. А.), Заказчик (напарник / бизнес-заказчик темы)

---

**[Исполнитель → Заказчик]**  
Добрый день. На основе SRS подготовила системные требования и оценки (см. таблицы A–B).

Предварительные сроки (1 разработчик, pd):  
- Registration/login — 5  
- Training session — 8  
- Metrics — 3  
- Setup — 2  
- Persist result — 3  
- History — 2  
- Leaderboard — 3  
- Admin CRUD texts — 4  

Суммарно MVP без Admin ≈ **26 pd** ≈ **3 итерации по 10 дней** при одном разработчике (с запасом на интеграцию/баги).  
Прошу подтвердить / скорректировать приоритеты (10…50).

---

**[Заказчик → Исполнитель]**  
История для меня важнее рейтинга — без прогресса продукт «мёртвый». Setup тоже нужен сразу, иначе тренировка однотипная.  

Но **8 дней на Training session** — это много. Почему так долго? Мы же просто «показываем текст и считаем ошибки». Admin CRUD в первой версии не нужен — тексты можно зашить в базу.  
Leaderboard можно позже.

Приоритеты с моей стороны: Auth+Training+Metrics = 10; Setup повыше; History выше Leaderboard; Admin — в конец.

---

**[Исполнитель → Заказчик]**  
Понимаю беспокойство по сроку Training session. «Просто текст» на деле включает: синхронизацию таймера, подсветку верных/ошибочных символов, отмену при обрыве сети, согласование метрик с сервером (иначе рейтинг легко накрутить). Это как раз то, за что пользователь платит вниманием.

Что можем сделать без потери ценности:  
1) в первой итерации — тренировка без live-WPM (только финальный экран) → экономия ~1.5–2 pd;  
2) Admin CRUD снимаем с MVP (как вы предложили) — экономия 4 pd;  
3) Leaderboard оставляем, но в 3-й итерации.

Успокаиваю по срокам: **дедлайн продукта не сдвигаем** — укладываемся в 3 итерации × 10 дней, если режем Admin и чуть упрощаем live-метрики. Ниже план v1.

---

**[Заказчик → Исполнитель]**  
Ок. Live-WPM всё же хочу в MVP — пусть лучше чуть подольше Training, но с «живыми» цифрами. Admin — отложить. History важнее Leaderboard. План итераций жду.

<a id="d-plan-v1"></a>
### D. План реализации по итерациям — v1 (после раунда 1)

**Iteration length:** 10 working days  
**Team:** 1 Regular developer  
**Deadline продукта (целевой):** конец Iteration 3  

| Iteration | Days | Scope (priority order) | Capacity fit |
|-----------|------|------------------------|--------------|
| **I1** | 1–10 | SYS-F-01 Auth (5) + SYS-F-03 Metrics stub (1) + start SYS-F-02 Training vertical slice: timer+highlight without persist (4) | 10 / 10 |
| **I2** | 11–20 | Finish SYS-F-02 (4) + SYS-F-04 Setup (2) + SYS-F-05 Persist (3) + polish NF-01 (1) | 10 / 10 |
| **I3** | 21–30 | SYS-F-06 History (2) + SYS-F-07 Leaderboard (3) + SYS-NF-03/04/05 (2.5) + integration buffer (2.5) | 10 / 10 |

**Out of v1 plan:** SYS-F-09 Admin CRUD (priority 50).  
**Dependency notes:** Auth before Persist/History/Leaderboard; Metrics before Result screen; Setup before varied Training content.

<a id="e-round2"></a>
### E. Переписка с Заказчиком — раунд 2

---

**[Заказчик → Исполнитель]**  
Передумала. **Admin не нужен**, но очень нужна возможность **загрузить свой текст** (например, фрагмент лекции) и тренироваться по нему. Это я раньше не сформулировала, а без этого отличия от «ещё одного monkeytype» мало. Сделайте, пожалуйста, в MVP.

И ещё: дедлайн **не сдвигаем**. Бюджет сильно не увеличиваем.

---

**[Исполнитель → Заказчик]**  
Новый функционал **Custom user texts** (SYS-F-08) оценён в **4 pd**: форма загрузки, проверка длины/языка, привязка к пользователю, выбор в Setup.

Варианты:  
A) Сдвинуть релиз на ~0.5 итерации;  
B) Выкинуть Leaderboard из MVP;  
C) Оставить Leaderboard, добавить custom texts, но взять **junior** на простые задачи (история, часть UI форм) — срок почти тот же, стоимость вырастет умеренно.

Предлагаю **C**: не двигаем deadline, Leaderboard сохраняем (вам он нужен для мотивации), custom texts включаем с ограничением ≤2000 символов.

---

**[Заказчик → Исполнитель]**  
**Дедлайн не двигаем.** Сильно дороже тоже нельзя. Leaderboard не выкидывать. Если junior реально поможет уложиться — ок, но покажите цифры.

---

**[Исполнитель → Заказчик]**  
Согласовано. Добавляем Junior на I2–I3 (простые экраны History/формы custom text под ревью Regular).  
Custom texts входят в I2/I3; Admin окончательно out.  
Ниже план v2 и стоимость (Приложение F–G). Прошу подтвердить.

---

**[Заказчик → Исполнитель]**  
Подтверждаю план v2 и стоимость с junior. Custom texts — must; Admin — не делаем; deadline без сдвига.

<a id="f-plan-v2"></a>
### F. План реализации по итерациям — v2 (согласованный)

**Iteration length:** 10 working days  
**Team:** 1 Regular + 1 Junior (Junior с I2)  
**Deadline:** без сдвига — конец I3  

| Iteration | Regular (pd) | Junior (pd) | Scope |
|-----------|--------------|-------------|-------|
| **I1** | 10 | 0 | SYS-F-01 Auth; SYS-F-03 Metrics core; SYS-F-02 slice (timer + highlight + live WPM skeleton) |
| **I2** | 8 | 4 | Finish SYS-F-02; SYS-F-04 Setup; SYS-F-05 Persist; **SYS-F-08 Custom texts** (Junior: form UI + validation under review) |
| **I3** | 7 | 5 | SYS-F-06 History (Junior UI); SYS-F-07 Leaderboard (Regular); NF polish; integration & demo buffer |

**Explicitly deferred:** SYS-F-09 Admin text CRUD.  
**Risk buffer:** 2.5 pd в I3 на стыковку custom texts ↔ setup ↔ training.

**Почему функция длиннее итерации не ломает план:** Training (8 pd) разрезана на I1 (скелет) + I2 (завершение + persist). Заказчику на демо I1 показываем работающий ввод/таймер без сохранения — ценность видна рано.

<a id="g-cost"></a>
### G. Сводка стоимости проекта

| Version | Regular pd | Junior pd | Cost (у.е.) | Deadline |
|---------|------------|-----------|-------------|----------|
| Plan v1 (1 dev, no custom texts) | 26 | 0 | 26×120 = **3120** | конец I3 |
| **Plan v2 согласованный** | 25 | 9 | 25×120 + 9×80 = **3000+720 = 3720** | конец I3 (без сдвига) |

Итого: дедлайн сохранён; стоимость +600 у.е. относительно v1 за счёт junior и SYS-F-08; Admin (4 pd) не оплачивается. Заказчик согласился (см. раунд 2).

<a id="h-control"></a>
### H. Контрольные вопросы (краткие ответы)

**1. Если оценка функции ≫ длины итерации?**  
Резать на вертикальные инкременты (тонкий сквозной срез), планировать на несколько итераций, на демо показывать частичную ценность; не обещать «всю функцию за одну итерацию».

**2. Заказчик добавил функционал во время разработки?**  
Оценить влияние на срок/стоимость; предложить trade-off (убрать/отложить другое, удлинить срок, добавить ресурс). Зафиксировать изменение в SRS/SDD и плане.

**3. Важный функционал в самом конце?**  
Озвучить риск сорвать релиз; предложить: post-release, замена другого scope, или отдельный hotfix-итерацию с явной доплатой. Не втискивать «тихо».

**4. Ролевые игры и наблюдение?**  
Роли Заказчик/Исполнитель выявляют скрытые ожидания; наблюдение за работой пользователя (как печатает) даёт нефункциональные и UX-требования, которые в интервью не звучат.

**5. Требования к user stories?**  
Инвест-критерии: Independent, Negotiable, Valuable, Estimable, Small, Testable; понятная ценность для пользователя и критерий приёмки.

**6. Когда остановить сбор требований сейчас?**  
Когда есть согласованный MVP, приоритеты, оценки, понятные acceptance criteria и дальнейшее уточнение даёт мало новой ценности относительно стоимости задержки разработки (diminishing returns).
