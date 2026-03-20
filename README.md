# Поисковый движок по документам

Учебный проект (Hexlet): полнотекстовый поиск по коллекции документов с обратным индексом и ранжированием по TF‑IDF и эвристикам релевантности.

### Hexlet tests and linter status:
[![Actions Status](https://github.com/Levasey/algorithms-project-69/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/Levasey/algorithms-project-69/actions)
[![Java CI](https://github.com/Levasey/algorithms-project-69/actions/workflows/build.yml/badge.svg)](https://github.com/Levasey/algorithms-project-69/actions/workflows/build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Levasey_algorithms-project-69&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Levasey_algorithms-project-69)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Levasey_algorithms-project-69&metric=bugs)](https://sonarcloud.io/summary/new_code?id=Levasey_algorithms-project-69)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=Levasey_algorithms-project-69&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=Levasey_algorithms-project-69)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Levasey_algorithms-project-69&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Levasey_algorithms-project-69)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=Levasey_algorithms-project-69&metric=duplicated_lines_density)](https://sonarcloud.io/summary/new_code?id=Levasey_algorithms-project-69)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=Levasey_algorithms-project-69&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=Levasey_algorithms-project-69)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Levasey_algorithms-project-69&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=Levasey_algorithms-project-69)

## Возможности

- **Обратный индекс** — слово → документы и частоты вхождений.
- **Многословный запрос** — в выдачу попадают документы, где встречается хотя бы одно слово из запроса; порядок сортировки учитывает «покрытие» запроса.
- **Стоп-слова** — из **многословного** запроса (два и больше токена после токенизации) убираются частые английские служебные слова (`the`, `is`, `a`, `at`, `me` и т.д.); однословный запрос не фильтруется (чтобы находить, например, `me`).
- **Спам-документы** — если `id` оканчивается на `_spam`, документ сортируется **ниже** любого обычного (даже при большем числе вхождений слов запроса), затем среди обычных и среди спам-документов действует общее ранжирование.
- **Токенизация** — слова из букв, цифр, `_` и апострофа (поддержка сокращений вроде `can't`, `don't`); регистр запроса и текста не различается.
- **Ранжирование** — сначала больше **разных** слов запроса, найденных в документе; затем больше **суммарных вхождений** этих слов; при равенстве — порядок документа в **исходном списке**; затем сумма **TF‑IDF**.

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
