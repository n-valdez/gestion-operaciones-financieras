package com.entidadfinanciera.operaciones.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("guardarSolicitudDelegate")
public class GuardarSolicitudDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        String solicitudId = (String) execution.getVariable("solicitudId");
        System.out.println(">>> El proceso de Camunda procesó la solicitud con ID: " + solicitudId);

    }
}