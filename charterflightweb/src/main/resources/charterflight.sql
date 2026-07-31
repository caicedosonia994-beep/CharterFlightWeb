-- =========================================================================
-- Charter Flight - Script de creacion de base de datos
-- =========================================================================

CREATE DATABASE IF NOT EXISTS charterflight
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE charterflight;

CREATE TABLE IF NOT EXISTS clientes (
    id_cliente  INT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(100) NOT NULL,
    apellido    VARCHAR(100) NOT NULL,
    documento   VARCHAR(50)  NOT NULL UNIQUE,
    telefono    VARCHAR(30)  NOT NULL,
    correo      VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
