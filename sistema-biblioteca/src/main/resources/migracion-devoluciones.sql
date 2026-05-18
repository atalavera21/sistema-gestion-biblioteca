-- =============================================================================
-- Migracion: Crear tabla devoluciones + datos de prueba
-- Ejecutar en MySQL: mysql -u root -padmin123 biblioteca_db < migracion-devoluciones.sql
-- NOTA: Hibernate (hbm2ddl.auto=update) crea la tabla automaticamente al arrancar.
--       Este script sirve como alternativa manual o para insertar datos de prueba
--       sin tener que re-ejecutar el seed completo.
-- =============================================================================

CREATE TABLE IF NOT EXISTS `devoluciones` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `prestamo_id` BIGINT NOT NULL,
    `fecha_devolucion` DATE NOT NULL,
    `dias_retraso` INT NOT NULL DEFAULT 0,
    `a_tiempo` TINYINT(1) NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_prestamo_id` (`prestamo_id`),
    CONSTRAINT `fk_devolucion_prestamo`
        FOREIGN KEY (`prestamo_id`) REFERENCES `prestamos` (`id`)
        ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================================================
-- Datos de prueba: prestamos historicos
-- Solo ejecutar si la BD ya tiene los libros y usuarios del seed principal.
-- =============================================================================

SET FOREIGN_KEY_CHECKS = 0;

-- Limpiar datos existentes antes de insertar
DELETE FROM `devoluciones`;
DELETE FROM `prestamos`;

INSERT INTO `prestamos` (`id`, `fechaPrestamo`, `fechaDevolucionEstimada`, `fechaDevolucionReal`, `estado`, `activo`, `libro_id`, `usuario_id`) VALUES
-- DEVUELTO a tiempo
(1,  '2026-03-20', '2026-04-03', '2026-04-01', 'DEVUELTO',   0,  2,  1),
(2,  '2026-03-25', '2026-04-08', '2026-04-07', 'DEVUELTO',   0,  3,  1),
(3,  '2026-04-01', '2026-04-15', '2026-04-14', 'DEVUELTO',   0,  5,  1),
(4,  '2026-04-05', '2026-04-19', '2026-04-18', 'DEVUELTO',   0,  6,  1),
(5,  '2026-04-08', '2026-04-22', '2026-04-20', 'DEVUELTO',   0, 11,  2),
(6,  '2026-04-12', '2026-04-26', '2026-04-25', 'DEVUELTO',   0, 12,  2),
(7,  '2026-04-15', '2026-04-29', '2026-04-28', 'DEVUELTO',   0, 15,  3),
(8,  '2026-04-20', '2026-05-04', '2026-05-02', 'DEVUELTO',   0, 16,  3),
(9,  '2026-04-25', '2026-05-09', '2026-05-08', 'DEVUELTO',   0, 17,  4),
(10, '2026-04-28', '2026-05-12', '2026-05-10', 'DEVUELTO',   0,  1,  4),
(11, '2026-04-20', '2026-05-04', '2026-05-01', 'DEVUELTO',   0,  3,  5),
(12, '2026-04-22', '2026-05-06', '2026-05-05', 'DEVUELTO',   0, 14,  2),
-- PENALIZADO (devuelto con retraso)
(13, '2026-03-15', '2026-03-29', '2026-04-10', 'PENALIZADO', 0,  1,  1),
(14, '2026-03-20', '2026-04-03', '2026-04-15', 'PENALIZADO', 0,  4,  1),
(15, '2026-04-01', '2026-04-15', '2026-04-28', 'PENALIZADO', 0, 13,  2),
(16, '2026-04-05', '2026-04-19', '2026-05-06', 'PENALIZADO', 0,  2,  5),
-- ACTIVO (en curso)
(17, '2026-05-05', '2026-05-19', NULL,          'ACTIVO',     1,  6,  4),
(18, '2026-05-08', '2026-05-22', NULL,          'ACTIVO',     1,  9,  5),
(19, '2026-05-10', '2026-05-24', NULL,          'ACTIVO',     1, 27,  1),
-- VENCIDO (atrasado, sin devolver)
(20, '2026-04-20', '2026-05-04', NULL,          'VENCIDO',    1,  7,  3);

INSERT INTO `devoluciones` (`id`, `prestamo_id`, `fechaDevolucion`, `diasRetraso`, `aTiempo`) VALUES
-- A tiempo
(1,  1,  '2026-04-01', 0,  1),
(2,  2,  '2026-04-07', 0,  1),
(3,  3,  '2026-04-14', 0,  1),
(4,  4,  '2026-04-18', 0,  1),
(5,  5,  '2026-04-20', 0,  1),
(6,  6,  '2026-04-25', 0,  1),
(7,  7,  '2026-04-28', 0,  1),
(8,  8,  '2026-05-02', 0,  1),
(9,  9,  '2026-05-08', 0,  1),
(10, 10, '2026-05-10', 0,  1),
(11, 11, '2026-05-01', 0,  1),
(12, 12, '2026-05-05', 0,  1),
-- Con retraso
(13, 13, '2026-04-10', 12, 0),
(14, 14, '2026-04-15', 12, 0),
(15, 15, '2026-04-28', 13, 0),
(16, 16, '2026-05-06', 17, 0);

SET FOREIGN_KEY_CHECKS = 1;
