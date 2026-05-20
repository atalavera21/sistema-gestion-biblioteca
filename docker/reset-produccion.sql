-- =============================================================================
-- reset-produccion.sql  ·  Reset al estado inicial — Biblioteca SENATI
--
-- Deja la BD como al inicio de una demo:
--   - prestamos, devoluciones, valoraciones VACIOS
--   - libros creados a mano (id > 35) borrados; los 35 del seed quedan
--   - usuarios sin penalizacion, puntuacion restaurada (50 estudiante / 100 admin)
--
-- NO restaura ediciones hechas a libros del seed (si editaste un titulo,
-- queda con la edicion). Tampoco toca categorias.
--
-- Uso (usar siempre con docker cp para evitar problemas de encoding):
--   docker cp docker\reset-produccion.sql biblioteca-db:/tmp/reset.sql
--   docker compose exec -T db bash -c 'MYSQL_PWD="$MYSQL_PASSWORD" mysql --default-character-set=utf8mb4 -u biblioteca biblioteca_db < /tmp/reset.sql'
-- =============================================================================

SET FOREIGN_KEY_CHECKS = 0;

-- 1. Vaciar tablas transaccionales
TRUNCATE TABLE valoraciones;
TRUNCATE TABLE devoluciones;
TRUNCATE TABLE prestamos;

-- 2. Borrar libros creados a mano durante demos (el seed inicial tiene 35 libros)
DELETE FROM libros WHERE id > 35;

-- 3. Limpiar el estado de los usuarios: sin penalizacion, puntuacion inicial
UPDATE usuarios
   SET penalizado = 0,
       puntuacion = CASE WHEN rol = 'ADMIN' THEN 100 ELSE 50 END;

SET FOREIGN_KEY_CHECKS = 1;

SELECT 'Reset completado: estado inicial del seed restaurado.' AS resultado;
