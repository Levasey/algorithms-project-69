# Поисковый движок по документам

Учебный проект (Hexlet): полнотекстовый поиск по коллекции документов с обратным индексом и ранжированием по TF‑IDF и эвристикам релевантности.

### Hexlet tests and linter status:
[![Actions Status](https://github.com/Levasey/algorithms-project-69/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/Levasey/algorithms-project-69/actions)
[![Java CI](https://github.com/Levasey/algorithms-project-69/actions/workflows/build.yml/badge.svg)](https://github.com/Levasey/algorithms-project-69/actions/workflows/build.yml)

## Возможности

- **Обратный индекс** — слово → документы и частоты вхождений.
- **Многословный запрос** — в выдачу попадают документы, где встречается хотя бы одно слово из запроса; порядок сортировки учитывает «покрытие» запроса.
- **Токенизация** — слова из букв, цифр, `_` и апострофа (поддержка сокращений вроде `can't`, `don't`); регистр запроса и текста не различается.
- **Ранжирование** — сначала больше **разных** слов запроса, найденных в документе; затем больше **суммарных вхождений** этих слов; затем выше **сумма TF‑IDF** по термам; при полном равенстве — порядок как в исходном списке документов.

## API

```java
List<String> ids = SearchEngine.search(docs, "shoot at me");
```

- **docs** — список карт с полями `"id"` (строковый идентификатор) и `"text"` (текст документа). Документы без `id`, без `text` или с пустым текстом игнорируются при индексации.
- **searchQuery** — поисковая строка; из неё извлекаются те же токены, что и из текстов.
- **результат** — список `id` отсортированный по убыванию релевантности.

## Требования

- JDK 17+ (рекомендуется LTS-версия, совместимая с Gradle в проекте)

## Сборка и тесты

Корень репозитория содержит Gradle-проект в каталоге **`app/`**. Команды выполняйте из `app/`:

| Команда | Описание |
|--------|----------|
| `./gradlew build` | Сборка, тесты, Checkstyle |
| `./gradlew test` | Только тесты |
| `./gradlew jacocoTestReport` | Отчёт покрытия: `app/build/reports/jacoco/test/html/index.html` |
| `./gradlew checkstyleMain` | Линтер |

Через **Makefile** (также из `app/`): `make test`, `make build`, `make lint`, `make report` и т.д.

## Структура

```
app/
├── src/main/java/hexlet/code/SearchEngine.java  — реализация
└── src/test/java/                                — JUnit 5
```

## Лицензия

Учебный проект в рамках курса Hexlet.
