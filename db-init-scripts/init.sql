-- db-init-scripts/init.sql

CREATE DATABASE IF NOT EXISTS identity_db;
CREATE DATABASE IF NOT EXISTS batch_management_db;
CREATE DATABASE IF NOT EXISTS traceability_db;

-- La corrección clave fue usar *.* para dar privilegios sobre todas las bases de datos.
GRANT ALL PRIVILEGES ON *.* TO 'foodchain_user'@'%';

FLUSH PRIVILEGES;