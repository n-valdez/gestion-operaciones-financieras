package com.entidadfinanciera.operaciones.application.service;

import com.entidadfinanciera.operaciones.infrastructure.adapters.output.persistence.repository.SolicitudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SolicitudService {

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Transactional
    public void registrarSolicitudDirecta(String id, String usuario, String tipo, Long monto, String moneda, String descripcion, String nivelRiesgo) {
        // Ejecuta una inserción SQL directa sin que Hibernate intente validar estados detached
        solicitudRepository.insertarSolicitudNativa(
                id,
                usuario,
                tipo,
                monto,
                moneda,
                descripcion,
                LocalDateTime.now(),
                "PENDIENTE",
                nivelRiesgo,
                0L
        );
    }
}