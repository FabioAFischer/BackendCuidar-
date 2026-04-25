package com.example.demo.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dtos.AlertaDTO;
import com.example.demo.services.AlertaService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/alerta")
@CrossOrigin(origins = "*")
public class AlertaController {

    private final AlertaService service;

    public AlertaController(AlertaService service) {
        this.service = service;
    }

    @Operation(
        summary = "Listar alertas",
        description = "Retorna uma lista paginada de alertas agendados"
    )
    @GetMapping("/listar_todas")
    public ResponseEntity<Page<AlertaDTO>> listarTodas(
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(service.listarAtivos(pageable));
    }

    @Operation(
        summary = "Buscar alerta por ID",
        description = "Retorna os dados de um alerta específico com base no ID informado"
    )
    @GetMapping("/listar/{id}")
    public ResponseEntity<AlertaDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(
        summary = "Cadastrar alerta",
        description = "Cria um novo alerta com os dados enviados no corpo da requisição"
    )
    @PostMapping("/cadastrar")
    public ResponseEntity<AlertaDTO> criar(@RequestBody AlertaDTO dto) {
        AlertaDTO criado = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @Operation(
        summary = "Atualizar alerta",
        description = "Atualiza os dados de um alerta existente com base no ID informado"
    )
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<AlertaDTO> atualizar(@PathVariable Integer id, @RequestBody AlertaDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @Operation(
        summary = "Deletar (cancelar) alerta",
        description = "Realiza a exclusão lógica (cancelamento) de um alerta com base no ID informado"
    )
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
