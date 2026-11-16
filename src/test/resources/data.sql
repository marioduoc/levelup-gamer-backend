INSERT INTO categoria (nombre) VALUES ('Juegos de Mesa'), ('Consolas');

INSERT INTO producto (codigo, nombre, descripcion, precio, oferta, stock, stock_critico, imagen, categoria_id) VALUES
  ('JM001', 'Catan', 'Juego de estrategia', 29990, 0, 25, 5, '/img/JM001.jpg', 1),
  ('JM002', 'Carcassonne', 'Juego de fichas', 24990, 20, 18, 5, '/img/JM002.jpg', 1),
  ('CO001', 'PlayStation 5', 'Consola', 549990, 0, 2, 3, '/img/CO001.jpg', 2);

-- Admin123! / Vendedor123! / Cliente123!
INSERT INTO usuario (run, nombre, apellidos, correo, password, rol, fecha_nacimiento, region, comuna, direccion, activo, creado_en) VALUES
  ('111111111', 'Ana', 'Rojas', 'admin@duoc.cl', '$2b$10$CqgoERBYIVE.yq4cIZBXpuRtd.FFbBq6dTXxjtYDcSy6HSTemT0.G', 'ADMIN', '1995-03-10', 'Región Metropolitana de Santiago', 'Santiago', 'Calle 1', TRUE, CURRENT_TIMESTAMP),
  ('222222222', 'Bruno', 'Soto', 'vendedor@duoc.cl', '$2b$10$9Oav5TGgtAIFNYMTKErnHe3qXd0OnTk1PtO4Rh5KVJyAsr7A3iFKK', 'VENDEDOR', '1998-07-22', 'Región Metropolitana de Santiago', 'Providencia', 'Calle 2', TRUE, CURRENT_TIMESTAMP),
  ('333333333', 'Camila', 'Muñoz', 'cliente@gmail.com', '$2b$10$JXvB.lqFFFv5rx/R7.Fk9OA0Qv/IB1iH0LNlj0lTZ.4bvPtzfJBYW', 'CLIENTE', '2001-11-05', 'Valparaíso', 'Viña del Mar', 'Calle 3', TRUE, CURRENT_TIMESTAMP);

INSERT INTO boleta (numero, fecha, subtotal, descuento, total, estado, usuario_id, nombre_cliente, apellidos_cliente, correo_cliente, calle, region, comuna) VALUES
  ('20261110-1001', '2026-11-10 15:30:00', 29990, 0, 29990, 'APROBADA', 3, 'Camila', 'Muñoz', 'cliente@gmail.com', 'Calle 3', 'Valparaíso', 'Viña del Mar');

INSERT INTO detalle_boleta (boleta_id, producto_codigo, nombre_producto, cantidad, precio_unitario, subtotal) VALUES
  (1, 'JM001', 'Catan', 1, 29990, 29990);
