-- =========================================================================
-- Charter Flight - Script de creacion de base de datos
-- =========================================================================

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

CREATE TABLE IF NOT EXISTS pagos (
    id_pago         INT AUTO_INCREMENT PRIMARY KEY,
    id_reserva      INT NOT NULL,
    id_cliente      INT NOT NULL,
    id_vuelo        INT NOT NULL,
    monto           DECIMAL(10,2) NOT NULL,
    estado          VARCHAR(30) NOT NULL DEFAULT 'Pendiente',
    fecha_pago      DATE NOT NULL,
    numero_factura  VARCHAR(50) NOT NULL UNIQUE,
CONSTRAINT fk_pagos_reserva FOREIGN KEY (id_reserva) REFERENCES reservas(id_reserva) ON DELETE RESTRICT,
    CONSTRAINT fk_pagos_cliente FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente) ON DELETE RESTRICT,
    CONSTRAINT fk_pagos_vuelo FOREIGN KEY (id_vuelo) REFERENCES vuelos(id_vuelo) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================================================================
-- Solicitudes de vuelos especiales (carga / corporativo)
-- No crea reservas confirmadas ni asigna tarifas automaticamente.
-- =========================================================================

CREATE TABLE IF NOT EXISTS solicitudes_especiales (
    id_solicitud        INT AUTO_INCREMENT PRIMARY KEY,
    nombre_contacto     VARCHAR(100) NOT NULL,
    correo_contacto     VARCHAR(100) NOT NULL,
    telefono_contacto   VARCHAR(30)  NOT NULL,
    tipo                VARCHAR(20)  NOT NULL,
    origen              VARCHAR(100) NOT NULL,
    destino             VARCHAR(100) NOT NULL,
    fecha_req           DATE         NOT NULL,
    tipo_servicio       VARCHAR(30)  NOT NULL,
    observaciones       TEXT,
    tipo_mercancia      VARCHAR(100),
    descripcion_carga   TEXT,
    motivo_viaje        VARCHAR(200),
    pasajeros_estimados INT,
    estado              VARCHAR(30)  NOT NULL DEFAULT 'Pendiente de revisión',
    fecha_solicitud     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
