-- Roles
INSERT INTO roles (nombre, descripcion) VALUES
                                            ('ADMIN',    'Acceso total: gestiona bodegas, productos, usuarios y auditorías'),
                                            ('EMPLEADO', 'Registra movimientos y consulta información');

-- Usuarios (contraseñas con BCrypt)
INSERT INTO usuarios (nombre, username, password, rol_id) VALUES
                                                              ('Administrador General', 'admin',     '$2a$10$B5ITRW4QXOW1G0IZ/DTr1uUPJhPiEv.Ny.mezwZExt2LcXmUsIVPy', 1),
                                                              ('Empleado de Prueba',    'empleado1', '$2a$10$cCMmgoezbXkbuZHYlA11Oe5C6mSvQH4.xzSFqxtABmGH94wFpfVJ.', 2);

-- Bodegas
INSERT INTO bodegas (nombre, ubicacion, capacidad, encargado) VALUES
                                                                  ('Bodega Central',  'Bogotá - Zona Industrial', 5000, 'Carlos Ramírez'),
                                                                  ('Bodega Norte',    'Medellín - Itagüí',        3000, 'Laura Gómez'),
                                                                  ('Bodega Occidente','Cali - Yumbo',             2500, 'Andrés Torres');

-- Productos
INSERT INTO productos (nombre, categoria, stock, precio) VALUES
                                                             ('Laptop Lenovo IdeaPad',   'Tecnología', 25, 2500000.00),
                                                             ('Mouse inalámbrico',       'Tecnología',  8,   45000.00),
                                                             ('Teclado mecánico',        'Tecnología', 40,  180000.00),
                                                             ('Silla ergonómica',        'Muebles',     6,  650000.00),
                                                             ('Escritorio de oficina',   'Muebles',    15,  480000.00),
                                                             ('Resma de papel carta',    'Papelería', 120,   14000.00),
                                                             ('Impresora láser',         'Tecnología',  9,  900000.00);

-- Inventario por bodega
INSERT INTO inventario (bodega_id, producto_id, cantidad) VALUES
                                                              (1, 1, 15), (2, 1, 10),
                                                              (1, 2,  5), (3, 2,  3),
                                                              (1, 3, 20), (2, 3, 10), (3, 3, 10),
                                                              (2, 4,  6),
                                                              (2, 5,  5), (3, 5, 10),
                                                              (1, 6, 60), (2, 6, 40), (3, 6, 20),
                                                              (1, 7,  9);

-- Movimientos de ejemplo
INSERT INTO movimientos (fecha, tipo, usuario_id, bodega_origen_id, bodega_destino_id, observacion) VALUES
                                                                                                        ('2026-11-02 09:15:00', 'ENTRADA',       2, NULL, 1, 'Ingreso de proveedor'),
                                                                                                        ('2026-11-05 14:30:00', 'SALIDA',        2, 2, NULL, 'Despacho a cliente'),
                                                                                                        ('2026-11-09 11:00:00', 'TRANSFERENCIA', 1, 1, 3,    'Reabastecimiento Occidente');

INSERT INTO detalle_movimiento (movimiento_id, producto_id, cantidad) VALUES
                                                                          (1, 3, 10), (1, 6, 20),
                                                                          (2, 1,  2), (2, 5,  1),
                                                                          (3, 6, 10), (3, 2,  3);

-- Auditorías de ejemplo
INSERT INTO auditorias (operacion, fecha_hora, usuario, entidad, entidad_id, valores_anteriores, valores_nuevos) VALUES
                                                                                                                     ('INSERT', '2026-11-01 08:00:00', 'admin', 'Bodega', 1, NULL,
                                                                                                                      '{"nombre":"Bodega Central","ubicacion":"Bogotá - Zona Industrial","capacidad":5000,"encargado":"Carlos Ramírez"}'),
                                                                                                                     ('UPDATE', '2026-11-03 10:20:00', 'admin', 'Producto', 2,
                                                                                                                      '{"precio":40000.00}', '{"precio":45000.00}');