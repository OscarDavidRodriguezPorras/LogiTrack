-- =====================================================
-- LogiTrack S.A. - FASE 1: Base de datos (MySQL 8+)
-- =====================================================

DROP DATABASE IF EXISTS logitrack;
CREATE DATABASE logitrack CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE logitrack;

-- -----------------------------------------------------
-- Roles propios (ADMIN / EMPLEADO)
-- -----------------------------------------------------
CREATE TABLE roles (
                       id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                       nombre      VARCHAR(30)  NOT NULL UNIQUE,
                       descripcion VARCHAR(150)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Usuarios (empleados que inician sesión)
-- -----------------------------------------------------
CREATE TABLE usuarios (
                          id             BIGINT AUTO_INCREMENT PRIMARY KEY,
                          nombre         VARCHAR(100) NOT NULL,
                          username       VARCHAR(50)  NOT NULL UNIQUE,
                          password       VARCHAR(100) NOT NULL,          -- hash BCrypt
                          rol_id         BIGINT       NOT NULL,
                          activo         BOOLEAN      NOT NULL DEFAULT TRUE,
                          fecha_creacion DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          CONSTRAINT fk_usuario_rol FOREIGN KEY (rol_id) REFERENCES roles(id)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Bodegas
-- -----------------------------------------------------
CREATE TABLE bodegas (
                         id        BIGINT AUTO_INCREMENT PRIMARY KEY,
                         nombre    VARCHAR(100) NOT NULL,
                         ubicacion VARCHAR(150) NOT NULL,
                         capacidad INT          NOT NULL,
                         encargado VARCHAR(100) NOT NULL,
                         CONSTRAINT ck_bodega_capacidad CHECK (capacidad > 0)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Productos (stock = total general de la empresa)
-- -----------------------------------------------------
CREATE TABLE productos (
                           id        BIGINT AUTO_INCREMENT PRIMARY KEY,
                           nombre    VARCHAR(100)  NOT NULL,
                           categoria VARCHAR(60)   NOT NULL,
                           stock     INT           NOT NULL DEFAULT 0,
                           precio    DECIMAL(12,2) NOT NULL,
                           CONSTRAINT ck_producto_stock  CHECK (stock >= 0),
                           CONSTRAINT ck_producto_precio CHECK (precio > 0)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Inventario: cuántas unidades de cada producto hay en cada bodega
-- -----------------------------------------------------
CREATE TABLE inventario (
                            id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                            bodega_id   BIGINT NOT NULL,
                            producto_id BIGINT NOT NULL,
                            cantidad    INT    NOT NULL DEFAULT 0,
                            CONSTRAINT uq_inventario UNIQUE (bodega_id, producto_id),
                            CONSTRAINT ck_inventario_cantidad CHECK (cantidad >= 0),
                            CONSTRAINT fk_inv_bodega   FOREIGN KEY (bodega_id)   REFERENCES bodegas(id),
                            CONSTRAINT fk_inv_producto FOREIGN KEY (producto_id) REFERENCES productos(id)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Movimientos de inventario (cabecera)
-- -----------------------------------------------------
CREATE TABLE movimientos (
                             id                BIGINT AUTO_INCREMENT PRIMARY KEY,
                             fecha             DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             tipo              ENUM('ENTRADA','SALIDA','TRANSFERENCIA') NOT NULL,
                             usuario_id        BIGINT NOT NULL,
                             bodega_origen_id  BIGINT NULL,
                             bodega_destino_id BIGINT NULL,
                             observacion       VARCHAR(255),
                             CONSTRAINT fk_mov_usuario FOREIGN KEY (usuario_id)        REFERENCES usuarios(id),
                             CONSTRAINT fk_mov_origen  FOREIGN KEY (bodega_origen_id)  REFERENCES bodegas(id),
                             CONSTRAINT fk_mov_destino FOREIGN KEY (bodega_destino_id) REFERENCES bodegas(id),
                             CONSTRAINT ck_mov_bodegas CHECK (
                                 (tipo = 'ENTRADA'       AND bodega_origen_id IS NULL     AND bodega_destino_id IS NOT NULL) OR
                                 (tipo = 'SALIDA'        AND bodega_origen_id IS NOT NULL AND bodega_destino_id IS NULL) OR
                                 (tipo = 'TRANSFERENCIA' AND bodega_origen_id IS NOT NULL AND bodega_destino_id IS NOT NULL
                                     AND bodega_origen_id <> bodega_destino_id)
                                 )
) ENGINE=InnoDB;

CREATE INDEX idx_mov_fecha ON movimientos(fecha);

-- -----------------------------------------------------
-- Detalle de movimiento (productos y cantidades)
-- -----------------------------------------------------
CREATE TABLE detalle_movimiento (
                                    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    movimiento_id BIGINT NOT NULL,
                                    producto_id   BIGINT NOT NULL,
                                    cantidad      INT    NOT NULL,
                                    CONSTRAINT ck_detalle_cantidad CHECK (cantidad > 0),
                                    CONSTRAINT fk_det_movimiento FOREIGN KEY (movimiento_id) REFERENCES movimientos(id),
                                    CONSTRAINT fk_det_producto   FOREIGN KEY (producto_id)   REFERENCES productos(id)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Auditoría
-- -----------------------------------------------------
CREATE TABLE auditorias (
                            id                BIGINT AUTO_INCREMENT PRIMARY KEY,
                            operacion         ENUM('INSERT','UPDATE','DELETE') NOT NULL,
                            fecha_hora        DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            usuario           VARCHAR(50) NOT NULL,
                            entidad           VARCHAR(50) NOT NULL,
                            entidad_id        BIGINT NULL,
                            valores_anteriores TEXT NULL,
                            valores_nuevos     TEXT NULL
) ENGINE=InnoDB;

CREATE INDEX idx_aud_usuario   ON auditorias(usuario);
CREATE INDEX idx_aud_operacion ON auditorias(operacion);