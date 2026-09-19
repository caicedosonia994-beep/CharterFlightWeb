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

CREATE TABLE IF NOT EXISTS vuelos (
    id_vuelo    INT AUTO_INCREMENT PRIMARY KEY,
    codigo      VARCHAR(20)  NOT NULL UNIQUE,
    origen      VARCHAR(100) NOT NULL,
    destino     VARCHAR(100) NOT NULL,
    fecha       DATE         NOT NULL,
    hora        TIME         NOT NULL,
    tipo        VARCHAR(30)  NOT NULL DEFAULT 'nacional',
    capacidad   INT          NOT NULL,
    estado      VARCHAR(30)  NOT NULL DEFAULT 'programado'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS reservas (
    id_reserva     INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente    INT NOT NULL,
    id_vuelo      INT NOT NULL,
    pasajeros     INT NOT NULL,
    fecha_reserva DATE NOT NULL,
    estado        VARCHAR(30) NOT NULL DEFAULT 'pendiente',
    CONSTRAINT fk_reservas_cliente FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente) ON DELETE RESTRICT,
    CONSTRAINT fk_reservas_vuelo FOREIGN KEY (id_vuelo) REFERENCES vuelos(id_vuelo) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
