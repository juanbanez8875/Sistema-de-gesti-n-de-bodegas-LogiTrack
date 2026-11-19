-- =============================================
--  LogiTrack S.A. - Datos Iniciales
--  Script DML para MySQL
-- =============================================

USE logitrack;

-- ---------------------------------------------
--  Usuarios (contraseñas cifradas con BCrypt)
--  admin    -> admin123
--  empleado  -> empleado123
-- ---------------------------------------------
INSERT INTO usuario (id, username, password, email, nombre, rol, enabled) VALUES
(1, 'admin',    '$2a$10$xfGB3cKBUnMyAcfLOD0IKOOGBfHYynHZgiuzpO6.eFrS2KCa0AsKm',
    'admin@logitrack.com',    'Administrador General', 'ADMIN',    TRUE),
(2, 'empleado', '$2a$10$CRBIjpRbu9Reap4/stsPFuxESmlKu6MGgjQy/BK6IKHki0oQeG/I6',
    'empleado@logitrack.com', 'Empleado Bodega Norte', 'EMPLEADO', TRUE);

-- ---------------------------------------------
--  Bodegas
-- ---------------------------------------------
INSERT INTO bodega (id, nombre, ubicacion, capacidad, encargado) VALUES
(1, 'Bodega Central',    'Av. Industrial 100, Guayaquil',  5000, 'Carlos Mendoza'),
(2, 'Bodega Norte',      'Km 15 Vía a la Costa, Manta',     3000, 'Ana Ruiz'),
(3, 'Bodega Sur',        'Panamericana Sur 200, Cuenca',   2000, 'Luis Tamayo');

-- ---------------------------------------------
--  Productos
-- ---------------------------------------------
INSERT INTO producto (id, nombre, categoria, stock, precio) VALUES
(1, 'Arroz 5kg',     'Alimentos',  150,  4.50),
(2, 'Aceite 1L',     'Alimentos',   80,  2.80),
(3, 'Detergente 1kg','Limpieza',     8,  3.20),
(4, 'Papel Higiénico','Limpieza',  200,  1.50),
(5, 'Atún Lata',     'Enlatados',   5,  1.80),
(6, 'Fideos 500g',   'Alimentos',  300,  1.20),
(7, 'Azúcar 1kg',    'Alimentos',   0,  1.10);

-- ---------------------------------------------
--  Inventario (stock por bodega)
-- ---------------------------------------------
INSERT INTO inventario (bodega_id, producto_id, cantidad) VALUES
(1, 1, 100),
(1, 2,  50),
(1, 3,   8),
(1, 4, 120),
(1, 5,   5),
(2, 1,  30),
(2, 6, 150),
(2, 7,   0),
(3, 1,  20),
(3, 4,  80),
(3, 6, 150);

-- ---------------------------------------------
--  Movimientos de ejemplo
-- ---------------------------------------------
INSERT INTO movimiento (id, fecha, tipo, usuario_id, bodega_origen_id, bodega_destino_id) VALUES
(1, '2026-11-01 09:30:00', 'ENTRADA',       2, NULL, 1),
(2, '2026-11-05 14:00:00', 'SALIDA',        2, 1,    NULL),
(3, '2026-11-10 10:00:00', 'TRANSFERENCIA', 1, 1,    2);

INSERT INTO detalle_movimiento (movimiento_id, producto_id, cantidad) VALUES
(1, 1, 100),
(1, 2,  50),
(2, 1,  20),
(3, 1,  30),
(3, 6,  50);
