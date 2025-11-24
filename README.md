# Java University Labs (Multitask/Concurrency)

Репозиторий лабораторных работ по Java для университета. Темы включают многозадачность/многопоточность, структуры данных и практику разработки.

## Структура
- Исходники: `src/`
- Исполняемые классы: классы `Main` в соответствующих папках заданий
- Артефакты сборки: `bin/` (игнорируется Git)

## Документация
- [Отчёт по лабораторной работе #10 (reportLab10.md)](reportLab10.md)
- [Отчёт по лабораторной работе #11 (reportLab11.md)](reportLab11.md)
- [Отчёт по лабораторной работе #12 (reportLab12.md)](reportLab12.md)

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
