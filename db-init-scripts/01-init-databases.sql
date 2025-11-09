-- Este script se ejecuta después de que el usuario y la DB principal (identity_db)
-- hayan sido creados por las variables de entorno de Docker.

-- Creamos las bases de datos adicionales para los otros servicios.
CREATE DATABASE IF NOT EXISTS batch_management_db;
CREATE DATABASE IF NOT EXISTS traceability_db;

-- ¡EL PASO CLAVE! Otorgamos todos los permisos al usuario 'foodchain_user'
-- sobre las nuevas bases de datos. El '%' significa que puede conectarse desde cualquier IP.
GRANT ALL PRIVILEGES ON `batch_management_db`.* TO 'foodchain_user'@'%';
GRANT ALL PRIVILEGES ON `traceability_db`.* TO 'foodchain_user'@'%';

-- Aplicamos los cambios de privilegios.
FLUSH PRIVILEGES;