-- Solo creamos las bases de datos para los futuros servicios.
-- 'identity_db' es creada automáticamente por las variables de entorno de Docker.
CREATE DATABASE IF NOT EXISTS batch_management_db;
CREATE DATABASE IF NOT EXISTS traceability_db;