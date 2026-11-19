-- =============================================
--  LogiTrack S.A. - Esquema de Base de Datos
--  Script DDL para MySQL
-- =============================================

CREATE DATABASE IF NOT EXISTS logitrack
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE logitrack;

-- ---------------------------------------------
--  Tabla: usuario
-- ---------------------------------------------
CREATE TABLE IF NOT EXISTS usuario (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(50)  NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL,
    email     VARCHAR(100) NOT NULL UNIQUE,
    nombre    VARCHAR(100) NOT NULL,
    rol       VARCHAR(20)  NOT NULL,
    enabled   BOOLEAN DEFAULT TRUE
);

-- ---------------------------------------------
--  Tabla: bodega
-- ---------------------------------------------
CREATE TABLE IF NOT EXISTS bodega (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre     VARCHAR(100) NOT NULL,
    ubicacion  VARCHAR(200),
    capacidad  INT,
    encargado  VARCHAR(100)
);

-- ---------------------------------------------
--  Tabla: producto
-- ---------------------------------------------
CREATE TABLE IF NOT EXISTS producto (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre    VARCHAR(100) NOT NULL,
    categoria VARCHAR(50),
    stock     INT DEFAULT 0,
    precio    DECIMAL(10,2)
);

-- ---------------------------------------------
--  Tabla: inventario (stock por bodega)
-- ---------------------------------------------
CREATE TABLE IF NOT EXISTS inventario (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    bodega_id   BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad    INT DEFAULT 0,
    UNIQUE KEY uk_bodega_producto (bodega_id, producto_id),
    FOREIGN KEY (bodega_id)   REFERENCES bodega(id),
    FOREIGN KEY (producto_id) REFERENCES producto(id)
);

-- ---------------------------------------------
--  Tabla: movimiento
-- ---------------------------------------------
CREATE TABLE IF NOT EXISTS movimiento (
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha             DATETIME NOT NULL,
    tipo              VARCHAR(20) NOT NULL,
    usuario_id        BIGINT,
    bodega_origen_id  BIGINT,
    bodega_destino_id BIGINT,
    FOREIGN KEY (usuario_id)        REFERENCES usuario(id),
    FOREIGN KEY (bodega_origen_id)  REFERENCES bodega(id),
    FOREIGN KEY (bodega_destino_id) REFERENCES bodega(id)
);

-- ---------------------------------------------
--  Tabla: detalle_movimiento
-- ---------------------------------------------
CREATE TABLE IF NOT EXISTS detalle_movimiento (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    movimiento_id BIGINT NOT NULL,
    producto_id   BIGINT NOT NULL,
    cantidad      INT NOT NULL,
    FOREIGN KEY (movimiento_id) REFERENCES movimiento(id),
    FOREIGN KEY (producto_id)   REFERENCES producto(id)
);

-- ---------------------------------------------
--  Tabla: auditoria
-- ---------------------------------------------
CREATE TABLE IF NOT EXISTS auditoria (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo_operacion     VARCHAR(20) NOT NULL,
    fecha_hora         DATETIME NOT NULL,
    usuario            VARCHAR(100),
    entidad            VARCHAR(100) NOT NULL,
    entidad_id         BIGINT,
    valores_anteriores TEXT,
    valores_nuevos     TEXT
);
