-- =============================================================================
-- fix-tildes.sql  ·  Corrige tildes ya corruptas en BD - Biblioteca SENATI
--
-- Para entornos (VPS o laptops) donde la BD se cargo con el seed original
-- (tildes corruptas) y ya tiene datos transaccionales que no se quieren perder.
--
-- Idempotente: si las tildes ya estan bien, los UPDATE no afectan nada
-- (el WHERE filtra por nombre actual).
--
-- Uso desde la raiz del proyecto:
--   docker cp docker\fix-tildes.sql biblioteca-db:/tmp/fix-tildes.sql
--   docker compose exec -T db bash -c 'MYSQL_PWD="$MYSQL_PASSWORD" mysql --default-character-set=utf8mb4 -u biblioteca biblioteca_db < /tmp/fix-tildes.sql'
-- =============================================================================

-- Categorias
UPDATE categorias SET nombre = 'Gestión'                WHERE nombre LIKE 'Gesti%n'      AND nombre <> 'Gestión';
UPDATE categorias SET nombre = 'Ingeniería de Software' WHERE nombre LIKE 'Ingenier%'    AND nombre <> 'Ingeniería de Software';
UPDATE categorias SET nombre = 'Programación'           WHERE nombre LIKE 'Programaci%n' AND nombre <> 'Programación';

-- Idioma en libros (cubre todas las variantes de corrupcion)
UPDATE libros SET idioma = 'Inglés'  WHERE idioma LIKE 'Ingl%' AND idioma <> 'Inglés';
UPDATE libros SET idioma = 'Español' WHERE idioma LIKE 'Espa%' AND idioma <> 'Español';

-- Nombres de estudiantes (se identifican por codigoUniversitario, que no tiene tildes)
UPDATE usuarios SET nombre = 'Ana Lucía Ramírez Torres'    WHERE codigoUniversitario = 'EST-002';
UPDATE usuarios SET nombre = 'Diego Fernández Paredes'     WHERE codigoUniversitario = 'EST-003';
UPDATE usuarios SET nombre = 'María José Villanueva'       WHERE codigoUniversitario = 'EST-004';
UPDATE usuarios SET nombre = 'Luis Alberto Sánchez Cruz'   WHERE codigoUniversitario = 'EST-005';

-- Tambien aprovechamos para arreglar libros con dias_prestamo = 0 o NULL
UPDATE libros SET dias_prestamo = 14 WHERE dias_prestamo IS NULL OR dias_prestamo = 0;

SELECT 'Tildes corregidas. Categorias, libros y usuarios actualizados.' AS resultado;
