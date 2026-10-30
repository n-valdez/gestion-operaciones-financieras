CREATE TABLE solicitudes (
                             id VARCHAR(50) PRIMARY KEY,
                             usuario_solicitante VARCHAR(100) NOT NULL,
                             tipo_operacion VARCHAR(50) NOT NULL,
                             monto BIGINT NOT NULL,
                             moneda VARCHAR(10) NOT NULL,
                             descripcion TEXT,
                             fecha_creacion TIMESTAMP NOT NULL,
                             estado VARCHAR(30) NOT NULL,
                             nivel_riesgo VARCHAR(20) NOT NULL,
                             version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE aprobaciones (
                              id BIGSERIAL PRIMARY KEY,
                              solicitud_id VARCHAR(50) REFERENCES solicitudes(id),
                              rol_requerido VARCHAR(50) NOT NULL,
                              usuario_aprobador VARCHAR(100),
                              estado VARCHAR(30) NOT NULL,
                              fecha_decision TIMESTAMP
);

CREATE TABLE historial_eventos (
                                   id BIGSERIAL PRIMARY KEY,
                                   solicitud_id VARCHAR(50) REFERENCES solicitudes(id),
                                   fecha TIMESTAMP NOT NULL,
                                   usuario VARCHAR(100) NOT NULL,
                                   estado_nuevo VARCHAR(30) NOT NULL,
                                   comentario TEXT
);