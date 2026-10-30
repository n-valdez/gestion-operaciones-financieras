package com.entidadfinanciera.operaciones.controller;

import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tareas")
public class TareaController {

    @Autowired
    private TaskService taskService;

    // Listar todas las tareas pendientes de aprobación en Camunda
    @GetMapping("/pendientes")
    public ResponseEntity<List<Map<String, Object>>> obtenerTareasPendientes() {
        List<Task> tasks = taskService.createTaskQuery().list();

        List<Map<String, Object>> resultado = tasks.stream().map(task -> {
            Map<String, Object> info = new HashMap<>();
            info.put("taskId", task.getId());
            info.put("nombreTarea", task.getName());
            info.put("asignadoA", task.getAssignee());
            info.put("processInstanceId", task.getProcessInstanceId());
            info.put("creacion", task.getCreateTime());
            return info;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }

    // Aprobar o rechazar una tarea específica por su ID
    @PostMapping("/{taskId}/completar")
    public ResponseEntity<String> completarTarea(
            @PathVariable String taskId,
            @RequestParam boolean aprobado,
            @RequestParam(required = false) String comentario) {

        Map<String, Object> variables = new HashMap<>();
        variables.put("aprobado", aprobado);
        if (comentario != null) {
            variables.put("comentario", comentario);
        }

        taskService.complete(taskId, variables);

        String estado = aprobado ? "APROBADA" : "RECHAZADA";
        return ResponseEntity.ok("¡Tarea " + taskId + " procesada con éxito! Estado: " + estado);
    }
}