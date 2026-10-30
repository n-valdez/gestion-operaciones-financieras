package com.entidadfinanciera.operaciones.infrastructure.adapters.output.persistence.repository;

import com.entidadfinanciera.operaciones.infrastructure.adapters.output.persistence.entity.SolicitudEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface SolicitudRepository extends JpaRepository<SolicitudEntity, String> {

    @Modifying
    @Query(value = "INSERT INTO solicitudes (id, usuario_solicitante, tipo_operacion, monto, moneda, descripcion, fecha_creacion, estado, nivel_riesgo, version) " +
            "VALUES (:id, :usuario, :tipo, :monto, :moneda, :descripcion, :fechaCreacion, :estado, :nivelRiesgo, :version)",
            nativeQuery = true)
    void insertarSolicitudNativa(
            @Param("id") String id,
            @Param("usuario") String usuario,
            @Param("tipo") String tipo,
            @Param("monto") Long monto,
            @Param("moneda") String moneda,
            @Param("descripcion") String descripcion,
            @Param("fechaCreacion") LocalDateTime fechaCreacion,
            @Param("estado") String estado,
            @Param("nivelRiesgo") String nivelRiesgo,
            @Param("version") Long version
    );
}