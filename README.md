# Level-Up Gamer - Backend

API REST de la tienda Level-Up Gamer. Proyecto de Desarrollo Fullstack II (DSY1104), Duoc UC.

Frontend: https://github.com/marioduoc/levelup-gamer-react

## Tecnologías

- Java 21, Spring Boot 4
- Spring Data JPA + MySQL 8
- Spring Security + JWT
- Swagger (springdoc-openapi)

## Cómo levantarlo

Necesitas Java 21 y una base MySQL con las tablas creadas.

```bash
./mvnw spring-boot:run
```

Variables de entorno (tienen valores por defecto para desarrollo):

| Variable | Por defecto |
|---|---|
| DB_URL | jdbc:mysql://localhost:3306/levelup |
| DB_USER | levelup |
| DB_PASSWORD | levelup |

## Modelo de datos

```
USUARIO 1 --< BOLETA 1 --< DETALLE_BOLETA >-- 1 PRODUCTO >-- 1 CATEGORIA
```
