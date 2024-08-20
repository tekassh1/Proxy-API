# Proxy API

Приложение для проксирования запросов на `jsonplaceholder.typicode.com`.
В приложении реализована авторизация с использованием **Spring Security**. Данные пользователей хранятся в БД.
Есть система ролей и различных прав доступа. Реализовано логирование на сервере,
с сохранением в БД. Также реализован inmemory-кэш для оптимизации количества запросов к целевому
ресурсу.

### Использованные технологии
* **Spring Boot**
* **Spring Security**
* **Docker**
* **PostgreSQL**
* **Spring Data JPA**
* **Maven**
* **Jackson**

### Эндпоинты
* `/signup` - зарегистрировать нового пользователя (роль по умолчанию для всех пользователей - `ROLE_USER`)
Формат запроса JSON:\
```
{
    "username": "username",
    "password": "password"
}
```

* `/admin/addUser` - добавить нового пользователя (доступно только пользователям с ролью `ROLE_ADMIN`)
* `/admin/setRole` - добавить роль существующему пользователю.
Формат запроса JSON:\
```
{
    "username": "username",
    "password": "password", (необязательно для `/admin/setRole`)
    "role": "role"
}
```

* GET `api/xxx` и `api/xxx/{id}` - получить данные указанной категории (все и по id).
* POST `api/xxx` - добавить новую запись.
* PUT `api/xxx/{id}` - обновить значение записи с заданным id.
* DELETE `api/xxx/{id}` - удалить запись с заданным id.

Форматы запросов JSON:
* `api/posts`:
```
{
    "userId": userId,
    "title": "title",
    "body": body
}
```
* `api/albums`:
```
{
    "userId": userId,
    "title": "title"
}
```
* `api/users`:
```
{
    "name": "name",
    "username": "username",
    "email": "email",
    "address": {
        "street": "street",
        "suite": "suite",
        "city": "city",
        "zipcode": "zipcode",
            "geo": {
                "lat": "lat",
                "lng": "lng"
            }
        },
        "phone": "phone",
        "website": "website",
        "company": {
            "name": "name",
            "catchPhrase": "catchPhrase",
            "bs": "bs"
        }
    }
```

### Расширенная ролевая модель
* `ROLE_ADMIN` - роль администратора, доступны все запросы.
* `ROLE_XXX` - роль для доступа ко всем запросам эндпоинта `XXX`.
* `ROLE_XXX_VIEWER` - роль для доступа к запросам GET эндпоинта `XXX`.
* `ROLE_XXX_EDITOR` - роль для доступа к запросам POST/PUT/DELETE эндпоинта `XXX`.
    
Учетная запись администратора создается (если ее нет) при запуске приложения.
Логин, пароль - `admin`.
Роль по умолчанию для нового пользователя - `ROLE_USER`. Пользователи с этими ролями не
имеют доступа к ресурсам `/api`. Роли доступа должны быть назначены администратором
при помощи `/admin/setRole`.

### Логирование
Действия пользователя сохраняются в базе данных. Формат записи: \
`| id | user_id | request_type | endpoint | param | request_time | http_status_code |`

### Кэширование
In-Memory кэш реализован при помощи `ConcurrentHashMap`.
Логика кэширования представлена ​​на изображении:

![CachingProcess](https://github.com/tekassh1/VKCloud-Intership/assets/90504722/793da8fb-f477-4a27-a558-2627c74bb8af)

Кроме того, используется метод `@Scheduled`, который проверяет валидность хэшей
каждые 5 секунд и, если значение невалидно, запрашивает обновление у целевого ресурса.

### Прочее
Также есть файл `docker-compose.yaml` для развёртывания приложения в **Docker**. Используются два образа — образ приложения
и образ PostgreSQL. В проекте используется JDK 17. Таблицы для приложения создаются в базе данных автоматически при помощи
`hibernate.ddl-auto=update`.
