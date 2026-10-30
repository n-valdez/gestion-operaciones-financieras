package com.entidadfinanciera.operaciones.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;
import java.time.LocalDateTime;

@Entity
@Table(name = "solicitudes")
@Getter
@Setter
public class SolicitudEntity implements Persistable<String> {

    @Id
    private String id;

    @Column(name = "usuario_solicitante", nullable = false)
    private String usuarioSolicitante;

    @Column(name = "tipo_operacion", nullable = false)
    private String tipoOperacion;

    @Column(nullable = false)
    private Long monto;

    @Column(nullable = false)
    private String moneda;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private String estado;

    @Column(name = "nivel_riesgo", nullable = false)
    private String nivelRiesgo;

    @Version
    @Column(nullable = false)
    private Long version;

    // --- Control para evitar el error "Detached entity passed to persist" ---
    @Transient
    private boolean nuevaEntidad = true;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isNew() {
        return this.nuevaEntidad || id == null;
    }

    @PostPersist
    @PostLoad
    void marcarComoNoNueva() {
        this.nuevaEntidad = false;
    }
}