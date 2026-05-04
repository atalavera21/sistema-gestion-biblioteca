-- ============================================================
-- Script de migracion: ldap_users + categorias iniciales
-- Ejecutar en MySQL (biblioteca_db)
-- ============================================================

-- 1. Migrar ldap_users: reemplazar estado (VARCHAR) por activo (BOOLEAN)
ALTER TABLE ldap_users ADD COLUMN activo TINYINT(1) NOT NULL DEFAULT 1;
UPDATE ldap_users SET activo = 1 WHERE estado = 'ACTIVO';
UPDATE ldap_users SET activo = 0 WHERE estado != 'ACTIVO';
ALTER TABLE ldap_users DROP COLUMN estado;

-- 2. Agregar columna rol a ldap_users
ALTER TABLE ldap_users ADD COLUMN rol VARCHAR(20) NOT NULL DEFAULT 'ESTUDIANTE';

-- 3. Actualizar usuario existente (U202312345) como ESTUDIANTE
UPDATE ldap_users SET rol = 'ESTUDIANTE' WHERE username = 'U202312345';

-- 4. Crear usuario admin
-- Password: admin123 (BCrypt)
INSERT INTO ldap_users (username, password, activo, rol)
VALUES ('admin', '$2a$10$zhOrVG1Yw0Jmd1GCXqU3Z.3CIR8d.tbHBTLf/J2uQhTKpKeDGLa4e', 1, 'ADMIN');

-- 5. Categorias iniciales (seeds para la nueva tabla categorias)
INSERT INTO categorias (nombre) VALUES ('Base de Datos');
INSERT INTO categorias (nombre) VALUES ('Desarrollo Web');
INSERT INTO categorias (nombre) VALUES ('Inteligencia Artificial');
INSERT INTO categorias (nombre) VALUES ('Redes');
INSERT INTO categorias (nombre) VALUES ('Seguridad');
INSERT INTO categorias (nombre) VALUES ('Sistemas Operativos');
INSERT INTO categorias (nombre) VALUES ('Programacion');
INSERT INTO categorias (nombre) VALUES ('Matematicas');
INSERT INTO categorias (nombre) VALUES ('Fisica');
INSERT INTO categorias (nombre) VALUES ('Otros');
