package com.entidadfinanciera.operaciones.delegate;

import com.entidadfinanciera.operaciones.infrastructure.adapters.output.persistence.entity.SolicitudEntity;
import com.entidadfinanciera.operaciones.infrastructure.adapters.output.persistence.repository.SolicitudRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("registrarSolicitudDelegate")
public class RegistrarSolicitudDelegate implements JavaDelegate {

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Long monto = (Long) execution.getVariable("monto");
        String nivelRiesgo = (String) execution.getVariable("nivelRiesgo");

        SolicitudEntity solicitud = new SolicitudEntity();
        solicitud.setMonto(monto);
        solicitud.setNivelRiesgo(nivelRiesgo != null ? nivelRiesgo : "BAJO");
        solicitud.setEstado("PENDIENTE");

        solicitudRepository.save(solicitud);

        System.out.println(">>> Solicitud guardada en PostgreSQL con éxito.");
    }
}