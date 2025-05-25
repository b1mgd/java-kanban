# Java Kanban Board

![Java](https://img.shields.io/badge/Java-17-orange)

Java Kanban Board – это REST API сервис для управления задачами, реализующий методологию Канбан. Проект разработан на чистом Java Core без использования фреймворков, демонстрируя применение принципов ООП и паттернов проектирования.

## 🚀 Функциональность

### Управление задачами
- Создание, чтение, обновление и удаление (CRUD) задач
- Управление статусами задач (NEW, IN_PROGRESS, DONE)
- Приоритизация задач
- История изменений задач
- Валидация пересечений по времени выполнения

### Управление эпиками и подзадачами
- Создание и управление эпиками
- Создание подзадач, связанных с эпиками
- Автоматическое обновление статуса эпика на основе статусов подзадач
- Расчет времени выполнения эпика

### Хранение данных
- In-memory хранение
- Сохранение в файл
- Поддержка работы с базой данных

## 🛠 Технологии

- Java 17
- JUnit 5 + Mockito (тестирование)
- Maven (сборка проекта)
- Git (версионный контроль)

## 📋 Требования

- JDK 17 или выше
- Maven 3.6+
- Для работы с БД: PostgreSQL 12+

## 🚀 Установка и запуск

1. Клонируйте репозиторий:
```bash
git clone https://github.com/b1mgd/java-kanban.git
```

2. Перейдите в директорию проекта:
```bash
cd java-kanban
```

3. Соберите проект:
```bash
mvn clean install
```

4. Запустите приложение:
```bash
java -jar target/java-kanban-1.0-SNAPSHOT.jar
```

## 📝 API Endpoints

### Задачи (Tasks)

#### Создать задачу
```http
POST /tasks
Content-Type: application/json

{
    "name": "Новая задача",
    "description": "Описание задачи",
    "duration": 60,
    "startTime": "2024-03-20T10:00:00"
}
```

#### Получить все задачи
```http
GET /tasks
Accept: application/json
```

#### Получить задачу по ID
```http
GET /tasks/{id}
Accept: application/json
```

#### Обновить задачу
```http
PUT /tasks/{id}
Content-Type: application/json

{
    "name": "Обновленная задача",
    "description": "Новое описание",
    "status": "IN_PROGRESS",
    "duration": 90,
    "startTime": "2024-03-20T11:00:00"
}
```

#### Удалить задачу
```http
DELETE /tasks/{id}
```

### Эпики (Epics)

#### Создать эпик
```http
POST /epics
Content-Type: application/json

{
    "name": "Новый эпик",
    "description": "Описание эпика"
}
```

#### Получить все эпики
```http
GET /epics
Accept: application/json
```

#### Получить эпик по ID
```http
GET /epics/{id}
Accept: application/json
```

#### Обновить эпик
```http
PUT /epics/{id}
Content-Type: application/json

{
    "name": "Обновленный эпик",
    "description": "Новое описание"
}
```

#### Удалить эпик
```http
DELETE /epics/{id}
```

### Подзадачи (Subtasks)

#### Создать подзадачу
```http
POST /subtasks
Content-Type: application/json

{
    "name": "Новая подзадача",
    "description": "Описание подзадачи",
    "epicId": 1,
    "duration": 30,
    "startTime": "2024-03-20T12:00:00"
}
```

#### Получить все подзадачи
```http
GET /subtasks
Accept: application/json
```

#### Получить подзадачу по ID
```http
GET /subtasks/{id}
Accept: application/json
```

#### Обновить подзадачу
```http
PUT /subtasks/{id}
Content-Type: application/json

{
    "name": "Обновленная подзадача",
    "description": "Новое описание",
    "status": "IN_PROGRESS",
    "duration": 45,
    "startTime": "2024-03-20T13:00:00"
}
```

#### Удалить подзадачу
```http
DELETE /subtasks/{id}
```

### История (History)

#### Получить историю просмотров
```http
GET /history
Accept: application/json
```

## 🧪 Тестирование

Проект включает модульные тесты, покрывающие основную функциональность:

```bash
# Запуск всех тестов
mvn test

# Запуск тестов с отчетом о покрытии
mvn verify
```

## 📦 Структура проекта

```
src/
├── main/java/
│   ├── model/          # Модели данных (Task, Epic, Subtask)
│   ├── service/        # Бизнес-логика
│   ├── storage/        # Реализации хранилищ
│   ├── util/           # Утилиты
│   └── http/           # HTTP сервер и обработчики
└── test/java/          # Модульные тесты
```



