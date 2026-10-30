package com.entidadfinanciera.operaciones.delegate;

import com.entidadfinanciera.operaciones.infrastructure.adapters.output.persistence.entity.SolicitudEntity;
import com.entidadfinanciera.operaciones.infrastructure.adapters.output.persistence.repository.SolicitudRepository;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("actualizarEstadoDelegate")
public class ActualizarEstadoDelegate implements JavaDelegate {

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        // Obtenemos el ID de la solicitud y la variable de aprobación
        String solicitudId = (String) execution.getVariable("solicitudId");
        Boolean aprobado = (Boolean) execution.getVariable("aprobado");

        String nombreActividad = execution.getCurrentActivityName();

        if (solicitudId != null) {
            SolicitudEntity solicitud = solicitudRepository.findById(solicitudId).orElse(null);
            if (solicitud != null) {
                String nuevoEstado;

                // Determinamos el estado
                if (aprobado != null && !aprobado) {
                    nuevoEstado = "RECHAZADA";
                } else if (nombreActividad != null && nombreActividad.contains("Riesgo")) {
                    nuevoEstado = "EN_EVALUACION_RIESGO";
                } else if (nombreActividad != null && nombreActividad.contains("Gerente")) {
                    nuevoEstado = "APROBADA_POR_GERENCIA";
                } else {
                    nuevoEstado = (aprobado != null && aprobado) ? "APROBADA" : "EN_PROCESO";
                }

                solicitud.setEstado(nuevoEstado);
                solicitudRepository.save(solicitud);
            }
        }
    }
}