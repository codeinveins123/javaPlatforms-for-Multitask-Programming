# Java University Labs (Multitask/Concurrency)

Репозиторий лабораторных работ по Java для университета. Темы включают многозадачность/многопоточность, структуры данных и практику разработки.

## Структура
- Исходники: `src/`
- Исполняемые классы: классы `Main` в соответствующих папках заданий
- Артефакты сборки: `bin/` (игнорируется Git)

## Документация
- [Отчёт по лабе (report.md)](report.md)

## Требования
- Java 8+ (наличие `javac` и `java` в PATH)

Компиляция отдельных пакетов по папкам:
```bash
javac -d bin -cp src src/path/to/package/*.java
```
## Запуск
```bash
java -cp bin fully.qualified.package.Main
```
Вывод можно перенаправить в файл:
```bash
java -cp bin fully.qualified.package.Main > result.txt
```
