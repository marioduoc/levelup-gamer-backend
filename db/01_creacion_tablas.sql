-- Level-Up Gamer - creación de tablas (MySQL 8)
-- Modelo: USUARIO -> BOLETA -> DETALLE_BOLETA <- PRODUCTO <- CATEGORIA
-- Este script lo ejecuta el contenedor de MySQL la primera vez que arranca
-- (docker-entrypoint-initdb.d). También se puede correr a mano en Workbench.

CREATE DATABASE IF NOT EXISTS levelup
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE levelup;

DROP TABLE IF EXISTS detalle_boleta;
DROP TABLE IF EXISTS boleta;
DROP TABLE IF EXISTS producto;
DROP TABLE IF EXISTS categoria;
DROP TABLE IF EXISTS usuario;

-- categorías del catálogo (Consolas, Accesorios, etc.)
CREATE TABLE categoria (
  id      INT          NOT NULL AUTO_INCREMENT,
  nombre  VARCHAR(50)  NOT NULL,
  CONSTRAINT pk_categoria PRIMARY KEY (id),
  CONSTRAINT uq_categoria_nombre UNIQUE (nombre)
);

-- productos, el código (ej JM001) es la llave igual que en el frontend
CREATE TABLE producto (
  codigo         VARCHAR(10)   NOT NULL,
  nombre         VARCHAR(100)  NOT NULL,
  descripcion    VARCHAR(500)  NULL,
  precio         INT           NOT NULL,
  oferta         INT           NOT NULL DEFAULT 0,   -- % de descuento (0 = sin oferta)
  stock          INT           NOT NULL DEFAULT 0,
  stock_critico  INT           NOT NULL DEFAULT 0,
  imagen         VARCHAR(255)  NULL,
  categoria_id   INT           NOT NULL,
  CONSTRAINT pk_producto PRIMARY KEY (codigo),
  CONSTRAINT fk_producto_categoria FOREIGN KEY (categoria_id) REFERENCES categoria (id),
  CONSTRAINT ck_producto_precio CHECK (precio >= 0),
  CONSTRAINT ck_producto_oferta CHECK (oferta BETWEEN 0 AND 100),
  CONSTRAINT ck_producto_stock CHECK (stock >= 0),
  CONSTRAINT ck_producto_stock_critico CHECK (stock_critico >= 0)
);

-- usuarios del sistema. rol: ADMIN, VENDEDOR o CLIENTE
-- la contraseña se guarda encriptada con BCrypt, nunca en texto plano
CREATE TABLE usuario (
  id                BIGINT        NOT NULL AUTO_INCREMENT,
  run               VARCHAR(9)    NOT NULL,
  nombre            VARCHAR(50)   NOT NULL,
  apellidos         VARCHAR(100)  NOT NULL,
  correo            VARCHAR(100)  NOT NULL,
  password          VARCHAR(100)  NOT NULL,
  rol               VARCHAR(20)   NOT NULL DEFAULT 'CLIENTE',
  fecha_nacimiento  DATE          NULL,
  region            VARCHAR(100)  NULL,
  comuna            VARCHAR(100)  NULL,
  direccion         VARCHAR(300)  NULL,
  activo            BOOLEAN       NOT NULL DEFAULT TRUE,
  creado_en         DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT pk_usuario PRIMARY KEY (id),
  CONSTRAINT uq_usuario_run UNIQUE (run),
  CONSTRAINT uq_usuario_correo UNIQUE (correo),
  CONSTRAINT ck_usuario_rol CHECK (rol IN ('ADMIN', 'VENDEDOR', 'CLIENTE'))
);

-- boleta = la orden de compra. guarda los datos de envío del checkout
CREATE TABLE boleta (
  id              BIGINT        NOT NULL AUTO_INCREMENT,
  numero          VARCHAR(20)   NOT NULL,              -- ej 20261116-4821
  fecha           DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
  subtotal        INT           NOT NULL,
  descuento       INT           NOT NULL DEFAULT 0,  -- 20 % para cuentas Duoc
  total           INT           NOT NULL,
  estado          VARCHAR(20)   NOT NULL,              -- APROBADA, RECHAZADA, ANULADA
  usuario_id      BIGINT        NOT NULL,
  nombre_cliente  VARCHAR(50)   NOT NULL,
  apellidos_cliente VARCHAR(100) NOT NULL,
  correo_cliente  VARCHAR(100)  NOT NULL,
  calle           VARCHAR(200)  NOT NULL,
  departamento    VARCHAR(50)   NULL,
  region          VARCHAR(100)  NOT NULL,
  comuna          VARCHAR(100)  NOT NULL,
  indicaciones    VARCHAR(300)  NULL,
  CONSTRAINT pk_boleta PRIMARY KEY (id),
  CONSTRAINT uq_boleta_numero UNIQUE (numero),
  CONSTRAINT fk_boleta_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id),
  CONSTRAINT ck_boleta_total CHECK (total >= 0 AND descuento >= 0 AND total = subtotal - descuento),
  CONSTRAINT ck_boleta_estado CHECK (estado IN ('APROBADA', 'RECHAZADA', 'ANULADA'))
);

-- cada línea de la boleta. se copia el nombre y el precio del momento de la compra
-- para que la boleta no cambie si después se edita el producto
CREATE TABLE detalle_boleta (
  id               BIGINT        NOT NULL AUTO_INCREMENT,
  boleta_id        BIGINT        NOT NULL,
  producto_codigo  VARCHAR(10)   NOT NULL,
  nombre_producto  VARCHAR(100)  NOT NULL,
  cantidad         INT           NOT NULL,
  precio_unitario  INT           NOT NULL,
  subtotal         INT           NOT NULL,
  CONSTRAINT pk_detalle_boleta PRIMARY KEY (id),
  CONSTRAINT fk_detalle_boleta FOREIGN KEY (boleta_id) REFERENCES boleta (id) ON DELETE CASCADE,
  CONSTRAINT fk_detalle_producto FOREIGN KEY (producto_codigo) REFERENCES producto (codigo),
  CONSTRAINT ck_detalle_cantidad CHECK (cantidad > 0),
  CONSTRAINT ck_detalle_precio CHECK (precio_unitario >= 0)
);
