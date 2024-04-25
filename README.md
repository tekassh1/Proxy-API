# Proxy API

An application for proxying requests to <https://jsonplaceholder.typicode.com/>.
The application implements user authentication using Spring Security, with user data stored in a database.
The application features a role-based system with various access privileges. Server actions are audited, with data stored in the database. An in-memory cache is implemented for optimizing the number of requests to the target resource.

## Technologies Used

* Spring Boot
* Spring Security
* Docker
* PostgreSQL
* Spring Data JPA
* Maven
* Jackson

## Endpoints

* `/signup` - register a new user (default role for all users is `ROLE_USER`)
* `/admin/addUser` - add a new user (available only to users with `ROLE_ADMIN` privileges)
* `/admin/setRole` - add a role to an existing user.

## Extended Role Model

* `ROLE_ADMIN` - role for administrators, with access to all requests.
* `ROLE_XXX` - role for access to all requests of the `XXX` endpoint.
* `ROLE_XXX_VIEWER` - role for access to GET requests of the `XXX` endpoint.
* `ROLE_XXX_EDITOR` - role for access to POST/PUT/DELETE requests of the `XXX` endpoint.

An administrator account is created (if not already existing) upon application startup.
Username, password - `admin`
The default role for new users is `ROLE_USER`. Users with these roles do not have access to `/api` resources.
Roles for access must be assigned by the administrator using `/admin/setRole`.

## Auditing Actions

User actions are stored in the database.
Record format: `| id | user_id | request_type | endpoint | param | request_time | http_status_code |`

## Caching

In-memory caching of target resource data is implemented using ConcurrentHashMap<>.
The caching process is illustrated in the following image:

![CachingProcess](https://github.com/tekassh1/VKCloud-Intership/assets/90504722/793da8fb-f477-4a27-a558-2627c74bb8af)

A `@Scheduled` method is used to check the validity of the cache every 5 seconds and request updates from the target resource if the value is invalid.

## Other

The project includes a `docker-compose.yaml` file for deployment in Docker. The project uses two Docker images - the application image and the PostgreSQL image. The project uses JDK 17.
Database tables for application operation are automatically created using `hibernate.ddl-auto=update`.
