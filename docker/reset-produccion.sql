-- =============================================================================
-- reset-produccion.sql  ·  Reset de datos para demos — Biblioteca SENATI
--
-- Mantiene intactos: categorias, libros, usuarios.
-- Borra por completo:  prestamos, devoluciones, valoraciones.
--
-- Uso en el servidor (ver guia DESPLIEGUE.md, Fase 9):
--   mysql -u biblioteca -p biblioteca_db < reset-produccion.sql
-- =============================================================================

SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE valoraciones;
TRUNCATE TABLE devoluciones;
TRUNCATE TABLE prestamos;

SET FOREIGN_KEY_CHECKS = 1;

SELECT 'Reset completado: prestamos, devoluciones y valoraciones vacios.' AS resultado;
