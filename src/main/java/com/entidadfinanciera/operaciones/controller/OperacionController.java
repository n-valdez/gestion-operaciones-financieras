package com.entidadfinanciera.operaciones.controller;

import com.entidadfinanciera.operaciones.application.service.SolicitudService;
import com.entidadfinanciera.operaciones.infrastructure.adapters.output.persistence.entity.SolicitudEntity;
import com.entidadfinanciera.operaciones.infrastructure.adapters.output.persistence.repository.SolicitudRepository;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/operaciones")
public class OperacionController {

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private SolicitudService solicitudService;

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private HistoryService historyService;

    @PostMapping("/iniciar")
    public ResponseEntity<String> iniciarSolicitud(
            @RequestParam String usuarioSolicitante,
            @RequestParam String tipoOperacion,
            @RequestParam Long monto,
            @RequestParam String moneda,
            @RequestParam String descripcion,
            @RequestParam String nivelRiesgo) {

        String solicitudId = UUID.randomUUID().toString();

        solicitudService.registrarSolicitudDirecta(
                solicitudId, usuarioSolicitante, tipoOperacion, monto, moneda, descripcion, nivelRiesgo
        );

        Map<String, Object> variables = new HashMap<>();
        variables.put("solicitudId", solicitudId);
        variables.put("usuarioSolicitante", usuarioSolicitante);
        variables.put("tipoOperacion", tipoOperacion);
        variables.put("monto", monto);
        variables.put("moneda", moneda);
        variables.put("descripcion", descripcion);
        variables.put("nivelRiesgo", nivelRiesgo);

        runtimeService.startProcessInstanceByKey("Process_19dobym", variables);

        return ResponseEntity.ok("¡Solicitud guardada en PostgreSQL e instancia de Camunda iniciada con éxito! ID: " + solicitudId);
    }

    // Cancelar una solicitud
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<String> cancelarSolicitud(@PathVariable String id) {
        SolicitudEntity solicitud = solicitudRepository.findById(id).orElse(null);
        if (solicitud == null) {
            return ResponseEntity.notFound().build();
        }

        solicitud.setEstado("CANCELADA");
        solicitudRepository.save(solicitud);

        runtimeService.createProcessInstanceQuery()
                .variableValueEquals("solicitudId", id)
                .list()
                .forEach(instance -> runtimeService.deleteProcessInstance(instance.getId(), "Cancelado por el usuario"));

        return ResponseEntity.ok("La solicitud " + id + " ha sido cancelada exitosamente.");
    }

    // Reenviar una solicitud corregida
    @PutMapping("/{id}/reenviar")
    public ResponseEntity<String> reenviarSolicitud(@PathVariable String id, @RequestBody SolicitudEntity datosActualizados) {
        SolicitudEntity solicitud = solicitudRepository.findById(id).orElse(null);
        if (solicitud == null) {
            return ResponseEntity.notFound().build();
        }

        // Actualizamos los campos necesarios para la corrección
        solicitud.setMonto(datosActualizados.getMonto());
        solicitud.setDescripcion(datosActualizados.getDescripcion());
        solicitud.setEstado("PENDIENTE");
        solicitudRepository.save(solicitud);

        return ResponseEntity.ok("La solicitud " + id + " ha sido corregida y reenviada para revisión.");
    }

    // Consultar historial y auditoría (Usando HistoryService de Camunda)
    @GetMapping("/historial/auditoria")
    public ResponseEntity<List<Map<String, Object>>> consultarHistorialAuditoria() {
        List<HistoricProcessInstance> historicInstances = historyService.createHistoricProcessInstanceQuery()
                .finished()
                .list();

        List<Map<String, Object>> historial = historicInstances.stream().map(instance -> {
            Map<String, Object> info = new HashMap<>();
            info.put("processInstanceId", instance.getId());
            info.put("processDefinitionId", instance.getProcessDefinitionId());
            info.put("inicio", instance.getStartTime());
            info.put("fin", instance.getEndTime());
            info.put("estado", instance.getState());
            return info;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(historial);
    }

    // Consultar el estado de las instancias de proceso activas
    @GetMapping("/instancias/activas")
    public ResponseEntity<List<Map<String, Object>>> consultarInstanciasActivas() {
        List<ProcessInstance> activeInstances = runtimeService.createProcessInstanceQuery().list();

        List<Map<String, Object>> activas = activeInstances.stream().map(instance -> {
            Map<String, Object> info = new HashMap<>();
            info.put("processInstanceId", instance.getId());
            info.put("processDefinitionId", instance.getProcessDefinitionId());
            info.put("isSuspended", instance.isSuspended());
            return info;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(activas);
    }

    // Consultar todas las solicitudes y sus estados
    @GetMapping
    public ResponseEntity<List<SolicitudEntity>> listarSolicitudes() {
        List<SolicitudEntity> solicitudes = solicitudRepository.findAll();
        return ResponseEntity.ok(solicitudes);
    }

    // Consultar una solicitud específica por su ID y su estado
    @GetMapping("/{id}")
    public ResponseEntity<SolicitudEntity> obtenerSolicitudPorId(@PathVariable String id) {
        return solicitudRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}