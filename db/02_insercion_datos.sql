-- Level-Up Gamer - datos iniciales (MySQL 8)
-- Mismo catálogo que tenía el frontend en src/datos/productos.js
-- Usuarios de prueba (contraseñas encriptadas con BCrypt, costo 10):
--   admin@duoc.cl     / Admin123!     -> ADMIN
--   vendedor@duoc.cl  / Vendedor123!  -> VENDEDOR
--   cliente@gmail.com / Cliente123!   -> CLIENTE

USE levelup;

SET NAMES utf8mb4;

INSERT INTO categoria (id, nombre) VALUES
  (1, 'Juegos de Mesa'),
  (2, 'Accesorios'),
  (3, 'Consolas'),
  (4, 'Computadores Gamers'),
  (5, 'Sillas Gamers'),
  (6, 'Mouse'),
  (7, 'Mousepad'),
  (8, 'Poleras Personalizadas');

INSERT INTO producto (codigo, nombre, descripcion, precio, oferta, stock, stock_critico, imagen, categoria_id) VALUES
  ('JM001', 'Catan', 'Un clásico juego de estrategia donde los jugadores compiten por colonizar y expandirse en la isla de Catan. Ideal para 3-4 jugadores.', 29990, 0, 25, 5, '/img/JM001.jpg', 1),
  ('JM002', 'Carcassonne', 'Un juego de colocación de fichas donde los jugadores construyen el paisaje alrededor de la fortaleza medieval de Carcassonne.', 24990, 20, 18, 5, '/img/JM002.jpg', 1),
  ('AC001', 'Controlador Inalámbrico Xbox Series X', 'Ofrece una experiencia de juego cómoda con botones mapeables y una respuesta táctil mejorada.', 59990, 0, 40, 8, '/img/AC001.jpg', 2),
  ('AC002', 'Auriculares Gamer HyperX Cloud II', 'Sonido envolvente de calidad con micrófono desmontable y almohadillas de espuma viscoelástica.', 79990, 15, 15, 5, '/img/AC002.jpg', 2),
  ('CO001', 'PlayStation 5', 'La consola de última generación de Sony, con gráficos impresionantes y tiempos de carga ultrarrápidos.', 549990, 0, 6, 3, '/img/CO001.jpg', 3),
  ('CG001', 'PC Gamer ASUS ROG Strix', 'Un potente equipo diseñado para los gamers más exigentes, con los últimos componentes.', 1299990, 10, 4, 2, '/img/CG001.jpg', 4),
  ('SG001', 'Silla Gamer Secretlab Titan', 'Diseñada para el máximo confort, con soporte ergonómico y personalización ajustable.', 349990, 0, 10, 3, '/img/SG001.jpg', 5),
  ('MS001', 'Mouse Gamer Logitech G502 HERO', 'Sensor de alta precisión y botones personalizables, ideal para un control preciso.', 49990, 0, 30, 8, '/img/MS001.jpg', 6),
  ('MP001', 'Mousepad Razer Goliathus Extended Chroma', 'Área de juego amplia con iluminación RGB personalizable y superficie suave y uniforme.', 29990, 0, 22, 5, '/img/MP001.jpg', 7),
  ('PP001', 'Polera Gamer Personalizada ''Level-Up''', 'Camiseta cómoda y estilizada, personalizable con tu gamer tag o diseño favorito.', 14990, 0, 0, 5, '/img/PP001.jpg', 8);

INSERT INTO usuario (id, run, nombre, apellidos, correo, password, rol, fecha_nacimiento, region, comuna, direccion) VALUES
  (1, '111111111', 'Ana', 'Rojas Pérez', 'admin@duoc.cl', '$2b$10$CqgoERBYIVE.yq4cIZBXpuRtd.FFbBq6dTXxjtYDcSy6HSTemT0.G', 'ADMIN', '1995-03-10', 'Región Metropolitana de Santiago', 'Santiago', 'Av. Libertador Bernardo O''Higgins 1234'),
  (2, '222222222', 'Bruno', 'Soto Díaz', 'vendedor@duoc.cl', '$2b$10$9Oav5TGgtAIFNYMTKErnHe3qXd0OnTk1PtO4Rh5KVJyAsr7A3iFKK', 'VENDEDOR', '1998-07-22', 'Región Metropolitana de Santiago', 'Providencia', 'Av. Providencia 2020'),
  (3, '333333333', 'Camila', 'Muñoz Vega', 'cliente@gmail.com', '$2b$10$JXvB.lqFFFv5rx/R7.Fk9OA0Qv/IB1iH0LNlj0lTZ.4bvPtzfJBYW', 'CLIENTE', '2001-11-05', 'Valparaíso', 'Viña del Mar', 'Calle Valparaíso 555');

-- una compra de ejemplo para que el vendedor tenga algo que revisar
INSERT INTO boleta (id, numero, fecha, subtotal, descuento, total, estado, usuario_id, nombre_cliente, apellidos_cliente, correo_cliente, calle, departamento, region, comuna, indicaciones) VALUES
  (1, '20261110-1001', '2026-11-10 15:30:00', 49982, 0, 49982, 'APROBADA', 3, 'Camila', 'Muñoz Vega', 'cliente@gmail.com', 'Calle Valparaíso 555', NULL, 'Valparaíso', 'Viña del Mar', 'Dejar en conserjería');

INSERT INTO detalle_boleta (boleta_id, producto_codigo, nombre_producto, cantidad, precio_unitario, subtotal) VALUES
  (1, 'JM001', 'Catan', 1, 29990, 29990),
  (1, 'JM002', 'Carcassonne', 1, 19992, 19992);
