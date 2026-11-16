# Level-Up Gamer - Backend

API REST de la tienda Level-Up Gamer. Proyecto de Desarrollo Fullstack II (DSY1104), Duoc UC.

Frontend: https://github.com/marioduoc/levelup-gamer-react

## Tecnologías

- Java 21, Spring Boot 4
- Spring Data JPA + MySQL 8
- Spring Security + JWT
- Swagger (springdoc-openapi)

## Levantar todo con Docker

Se necesitan los dos repos en la misma carpeta:

```
carpeta/
├── levelup-gamer-backend/   (este repo)
└── levelup-gamer-react/
```

```bash
cp .env.example .env        # cambiar claves y secreto JWT
docker compose up -d --build
```

| Contenedor | Qué hace |
|---|---|
| mysql | MySQL 8.4; la primera vez ejecuta los scripts de `db/` (tablas y datos) |
| backend | esta API (Spring Boot) en el puerto 8080 interno |
| frontend | nginx con el build de React; redirige `/api` y Swagger al backend |

Queda en `http://localhost` (o el puerto de `PUERTO_WEB`). Swagger: `/swagger-ui/index.html`.

## Levantarlo solo para desarrollo

Necesitas Java 21 y una base MySQL con las tablas de `db/`.

```bash
./mvnw spring-boot:run      # http://localhost:8080
./mvnw test                 # pruebas (usan H2 en memoria, no necesitan MySQL)
```

Variables de entorno (tienen valores por defecto para desarrollo):

| Variable | Por defecto |
|---|---|
| DB_URL | jdbc:mysql://localhost:3306/levelup |
| DB_USER | levelup |
| DB_PASSWORD | levelup |
| JWT_SECRET | clave de desarrollo (en producción va en `.env`) |

## Modelo de datos

```
USUARIO 1 --< BOLETA 1 --< DETALLE_BOLETA >-- 1 PRODUCTO >-- 1 CATEGORIA
```

## Usuarios de prueba

| Correo | Contraseña | Rol |
|---|---|---|
| admin@duoc.cl | Admin123! | ADMIN |
| vendedor@duoc.cl | Vendedor123! | VENDEDOR |
| cliente@gmail.com | Cliente123! | CLIENTE |
