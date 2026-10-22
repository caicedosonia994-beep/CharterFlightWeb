ALTER TABLE solicitudes_especiales
    ADD COLUMN avion_solicitado VARCHAR(30) NULL DEFAULT NULL,
    ADD CONSTRAINT chk_solicitudes_avion_solicitado
        CHECK (
            avion_solicitado IS NULL
            OR avion_solicitado IN ('Beechcraft 1900D', 'LET L-410', 'Piper Navajo')
        );

CREATE TABLE operaciones_cobrables (
    id_operacion INT NOT NULL AUTO_INCREMENT,
    tipo_operacion ENUM('VUELO_PROGRAMADO', 'SOLICITUD_ESPECIAL') NOT NULL,
    id_vuelo INT NULL,
    id_solicitud INT NULL,
    valor_total DECIMAL(10,2) NULL,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_operacion),
    UNIQUE KEY uq_operacion_vuelo (id_vuelo),
    UNIQUE KEY uq_operacion_solicitud (id_solicitud),
    CONSTRAINT fk_operacion_vuelo
        FOREIGN KEY (id_vuelo) REFERENCES vuelos(id_vuelo) ON DELETE RESTRICT,
    CONSTRAINT fk_operacion_solicitud
        FOREIGN KEY (id_solicitud) REFERENCES solicitudes_especiales(id_solicitud) ON DELETE RESTRICT,
    CONSTRAINT chk_operacion_un_solo_origen
        CHECK (
            (tipo_operacion = 'VUELO_PROGRAMADO' AND id_vuelo IS NOT NULL AND id_solicitud IS NULL)
            OR
            (tipo_operacion = 'SOLICITUD_ESPECIAL' AND id_solicitud IS NOT NULL AND id_vuelo IS NULL)
        ),
    CONSTRAINT chk_operacion_valor_total
        CHECK (valor_total IS NULL OR valor_total >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE movimientos_pago (
    id_movimiento INT NOT NULL AUTO_INCREMENT,
    id_operacion INT NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    fecha_pago DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    medio_pago ENUM(
        'Transferencia bancaria',
        'Tarjeta débito',
        'Tarjeta de crédito',
        'Efectivo',
        'Nequi',
        'Daviplata',
        'PSE'
    ) NOT NULL,
    referencia_transaccion VARCHAR(100) NULL,
    estado ENUM('Confirmado', 'Rechazado') NOT NULL DEFAULT 'Confirmado',
    clave_idempotencia VARCHAR(64) NOT NULL,
    PRIMARY KEY (id_movimiento),
    UNIQUE KEY uq_movimiento_idempotencia (clave_idempotencia),
    KEY ix_movimiento_operacion_fecha (id_operacion, fecha_pago),
    CONSTRAINT fk_movimiento_operacion
        FOREIGN KEY (id_operacion) REFERENCES operaciones_cobrables(id_operacion) ON DELETE RESTRICT,
    CONSTRAINT chk_movimiento_monto_positivo
        CHECK (monto > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
