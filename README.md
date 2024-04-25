# Proxy API

Application for proxying requests to https://jsonplaceholder.typicode.com/.
The application implements authorization using Spring Security. User data is stored in the database.
There is a system of roles and various access rights. Audit of actions on the server is implemented,
with data saved to the database. Also, inmemory cache is implemented to optimize the number of requests to the target
resource.

### Used Technologies
* Spring Boot
* Spring Security
* Docker
* PostgreSQL
* Spring Data JPA
* Maven
* Jackson

### Endpoints
* `/signup` - register a new user (default role for all users is `ROLE_USER`)
Request body JSON format:
```
{
    "username": "username",
    "password": "password"
}
```

* `/admin/addUser` - add a new user (available only for users with `ROLE_ADMIN` privileges)
* `/admin/setRole` - add a role to an existing user. \
Request body JSON format:
```
{
    "username": "username",
    "password": "password", (not necessary for `/admin/setRole`)
    "role": "role"
}
```

* GET `api/xxx` and `api/xxx/{id}` - get data of a specified category (all and by id).
* POST `api/xxx` - add a new entry.
* PUT `api/xxx/{id}` - update the value of an entry with the specified id.
* DELETE `api/xxx/{id}` - delete an entry with the specified id.

JSON request formats:
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

### Extended Role Model
* `ROLE_ADMIN` - administrator role, all requests are accessible.
* `ROLE_XXX` - role for accessing all requests of endpoint `XXX`.
* `ROLE_XXX_VIEWER` - role for accessing GET requests of endpoint `XXX`.
* `ROLE_XXX_EDITOR` - role for accessing POST/PUT/DELETE requests of endpoint `XXX`.
    
An administrator account is created (if it does not exist) when the application starts.
Login, password - `admin`
Default role for a new user - `ROLE_USER`. Users with these roles do not
have access to `/api` resources. Access roles must be assigned by the administrator
using `/admin/setRole`.

### Action Audit
User actions are saved in the database. Record format: \
`| id | user_id | request_type | endpoint | param | request_time | http_status_code |`

### Caching
Inmemory caching of target resource data is implemented using ConcurrentHashMap<>.
The cache logic is presented in the image:

![CachingProcess](https://github.com/tekassh1/VKCloud-Intership/assets/90504722/793da8fb-f477-4a27-a558-2627c74bb8af)

In addition, a `@Scheduled` method is used, which checks the validity of the hashes
every 5 seconds and, if the value is not valid, requests an update from the target resource.

### Miscellaneous
There is a `docker-compose.yaml` file for deployment in Docker. Two images are used - application image
and PostgreSQL image. JDK 17 is used in the project. Tables for the application are created in the database automatically using
`hibernate.ddl-auto=update`.
