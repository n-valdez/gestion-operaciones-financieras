package com.entidadfinanciera.operaciones;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OperacionesApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
		// Valida que el contexto de Spring Boot, Camunda y la base de datos carguen correctamente
	}

	@Test
	void testListarSolicitudesEndpoint() throws Exception {
		mockMvc.perform(get("/api/operaciones"))
				.andExpect(status().isOk());
	}

	@Test
	void testRegistroYSolicitudInvalida() throws Exception {
		// Valida que al buscar un ID inexistente devuelva 404 Not Found
		mockMvc.perform(get("/api/operaciones/uuid-falso-999"))
				.andExpect(status().isNotFound());
	}
}