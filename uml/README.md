# UML — динамическое поведение TypePulse (лаб. №5)

| Артефакт | Файл |
|----------|------|
| Глоссарий | [glossary.md](glossary.md) |
| Дополнительные спецификации | [additional-specs.md](additional-specs.md) |
| Описания актёров и прецедентов | [use-cases.md](use-cases.md) |
| Use Case | [diagrams/01-use-case.puml](diagrams/01-use-case.puml) → [png/01-use-case.png](png/01-use-case.png) |
| Activity | [diagrams/02-activity-training.puml](diagrams/02-activity-training.puml) → [png/02-activity-training.png](png/02-activity-training.png) |
| State | [diagrams/03-state-session.puml](diagrams/03-state-session.puml) → [png/03-state-session.png](png/03-state-session.png) |
| Sequence | [diagrams/04-sequence-training.puml](diagrams/04-sequence-training.puml) → [png/04-sequence-training.png](png/04-sequence-training.png) |

Перегенерация PNG (нужны JDK и `plantuml.jar` локально, в Git не коммитится из‑за `*.jar` в `.gitignore`):

```bash
java -jar uml/plantuml.jar -tpng -o ../png uml/diagrams/*.puml
```
