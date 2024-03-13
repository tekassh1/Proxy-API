# Proxy API

Приложение для проксирования запросов к https://jsonplaceholder.typicode.com/.
В приложении реализована авторизация посредством Spring Security. Данные пользователей хранятся в БД.
Имеется система ролей и различных прав доступа. Реализовано ведение аудита действий на сервере, с 
сохранением данных в БД. Также реализован inmemory кэш для оптимизации количества запросов к целевому 
ресурсу. 

### Использованные технологии
* Spring Boot
* Spring Security
* Docker
* PostgreSQL
* Spring Data JPA
* Maven
* Jackson

### Эндпоинты
* `/signup` - регистрация нового пользователя (роль для всех пользователей по умолчанию `ROLE_USER`)
Формат тела запроса JSON:
```
{
    "username": "username",
    "password": "password"
}
```

* `/admin/addUser` - добавление нового пользователя (доступно только для пользователей с правами `ROLE_ADMIN`)
* `/admin/setRole` - добавить роль имеющемуся пользователю. \
Формат тела запроса JSON:
```
{
    "username": "username",
    "password": "password", (не обязательно для `/admin/setRole`)
    "role": "role"
}
```

* GET `api/xxx` и `api/xxx/{id}` - получить данные заданной категории (все и по id).
* POST `api/xxx` - добавить новую запись.
* PUT `api/xxx/{id}` - обновить значение записи с указанным id.
* DELETE `api/xxx/{id}` - удалить запись с указанным id.

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
    
Учетная запись администратора создается (если не существует) при старте приложения.
Логин, пароль - `admin`
Роль по умолчанию для нового пользователя - `ROLE_USER`. Пользователи с этими ролями не
имеют доступа к `/api` ресурсам. Роли для доступа должен назначить администратор 
при помощи `/admin/setRole`.

### Аудит действий
Действия пользователя сохраняются в БД. Формат записи: \
`| id | user_id | request_type | endpoint | param | request_time | http_status_code |`

### Кэширование
Реализовано inmemory кэширование данных целевого ресурса посредством ConcurrentHashMap<>.
Логика работы хэша представлена на картинке:

![CachingProcess](https://github.com/tekassh1/VKCloud-Intership/assets/90504722/793da8fb-f477-4a27-a558-2627c74bb8af)


Помимо этого используется `@Scheduled` метод, которая проверяет валидность хэшей
каждые 5 секунд и, если значение не валидно, запрашивает обновление с целевого ресурса. 

### Прочее
Имеется файл `docker-compose.yaml` для развертывания в Docker. Используется два образа - образ приложения 
и образ PostgreSQL. В проекте используется JDK 17. Таблицы для работы приложения создаются в БД автоматически при помощи 
`hibernate.ddl-auto=update`.
