# Как сдать лабораторную №3 — пошагово

Репозиторий: `C:\Users\luboc\SDLC` (github.com/lubochko/SDLC)  
Тема: **TypePulse** — веб-тренажёр скорости печати (Java)

---

## Шаг 0. Сказать преподавателю (вариант проекта)

> Проект на курс: **TypePulse** — Web-приложение, Java (Spring Boot),  
> интерактивный тренажёр скорости печати: WPM, точность, история, рейтинг.  
> Границы MVP: без мобильного native, без ИИ, без онлайн-дуэлей.

---

## Шаг 1. Артефакты (уже подготовлены в ветке)

| Что | Куда |
|-----|------|
| Заполненный SRS | `srs/typepulse-srs.md` |
| SysML-модели | `srs/sysml/TypePulseContext.sysml`, `TypePulseUI.sysml` |
| PNG-мокапы | `srs/mockups/01-home.png` … `05-history-leaderboard.png` |
| HTML для пересъёмки мокапов | `srs/mockups/wireframes.html` |
| Замечания напарнику | `docs/lab3/partner-queue-feedback.md` |
| Ответы на контрольные | `docs/lab3/control-questions.md` |

Шаблон `srs/srs-template.md` **не затираем** — работаем в отдельном файле `typepulse-srs.md`.

---

## Шаг 2. Git: ветка → коммит → push → PR

```powershell
cd C:\Users\luboc\SDLC
git checkout lab3-business-requirements
git add srs/typepulse-srs.md srs/sysml srs/mockups docs/lab3 scripts README.md .gitignore
git commit -m "docs(lab3): бизнес-требования TypePulse, SysML и мокапы GUI"
git push -u origin lab3-business-requirements
```

На GitHub: **Pull Request** из `lab3-business-requirements` → `main`.  
В описании PR: «Лаб. №3: SRS TypePulse. Прошу review напарника и преподавателя.»

Попроси напарника оставить 2–4 комментария (можно из `partner-queue-feedback.md`).  
Сам оставь комментарии к PR напарника по его очереди.

---

## Шаг 3. После замечаний

1. Внести правки в `typepulse-srs.md`.
2. Строка в «История изменений», версия 0.2.
3. Новый коммит в ту же ветку (PR обновится).
4. В обсуждении PR кратко: какие замечания приняты / какие отклонены и почему.

---

## Шаг 4. На паре с преподавателем

- Показать SRS + PNG + SysML.
- Быть готовой к фразе: «добавьте X / уберите Y» — правите до конца пары (новый коммит).
- Ответить на контрольные вопросы.

---

## Если нужно переснять PNG

Открой `srs/mockups/wireframes.html` в браузере и сделай скриншоты блоков экранов  
или снова запусти скрипт экспорта (если есть `scripts/export-mockups.mjs`).
