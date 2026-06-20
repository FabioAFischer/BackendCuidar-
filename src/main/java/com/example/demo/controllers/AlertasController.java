package com.example.demo.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dtos.AlertasDTO;
import com.example.demo.services.AlertasService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping({"/alerta", "/alertas"})
@CrossOrigin(origins = "*")
public class AlertasController {

    private final AlertasService service;

    public AlertasController(AlertasService service) {
        this.service = service;
    }

    @Operation(
        summary = "Listar alertas",
        description = "Retorna uma lista paginada de alertas nao cancelados dos idosos vinculados ao cuidador autenticado"
    )
    @GetMapping("/listar_todos")
    public ResponseEntity<Page<AlertasDTO>> listarAlertas(
            @PageableDefault(size = 10, sort = "id") Pageable pageable,
            Authentication authentication) {
        return ResponseEntity.ok(service.listarAlertasAtivosDoCuidador(extrairCuidadorIdAutenticado(authentication), pageable));
    }

    @Operation(
        summary = "Listar alertas do idoso autenticado",
        description = "Retorna alertas nao cancelados do idoso autenticado no app"
    )
    @GetMapping("/me")
    public ResponseEntity<Page<AlertasDTO>> listarAlertasDoUsuario(
            @PageableDefault(size = 20, sort = "data_agendade") Pageable pageable,
            Authentication authentication) {
        return ResponseEntity.ok(service.listarAlertasDoIdoso(extrairUsuarioIdAutenticado(authentication), pageable));
    }

    @Operation(
        summary = "Listar alertas por idoso",
        description = "Retorna alertas nao cancelados de um idoso vinculado ao cuidador autenticado"
    )
    @GetMapping("/idoso/{idosoId}")
    public ResponseEntity<Page<AlertasDTO>> listarAlertasPorIdoso(
            @PathVariable Integer idosoId,
            @PageableDefault(size = 10, sort = "id") Pageable pageable,
            Authentication authentication) {
        return ResponseEntity.ok(service.listarAlertasPorIdoso(idosoId, extrairCuidadorIdAutenticado(authentication), pageable));
    }

    @Operation(
        summary = "Buscar alerta por ID",
        description = "Retorna os dados de um alerta de idoso vinculado ao cuidador autenticado"
    )
    @GetMapping("/listar/{id}")
    public ResponseEntity<AlertasDTO> buscarAlertaPorId(@PathVariable int id, Authentication authentication) {
        return ResponseEntity.ok(service.buscarAlertaPorId(id, extrairCuidadorIdAutenticado(authentication)));
    }

    @Operation(
        summary = "Cadastrar alerta",
        description = "Cria um novo alerta para um idoso vinculado ao cuidador autenticado"
    )
    @PostMapping("/cadastrar")
    public ResponseEntity<AlertasDTO> criarAlerta(@RequestBody AlertasDTO dto, Authentication authentication) {
        AlertasDTO criado = service.criarAlerta(dto, extrairCuidadorIdAutenticado(authentication));
        return ResponseEntity.status(HttpStatus.CREATED).body(criado);
    }

    @Operation(
        summary = "Atualizar alerta",
        description = "Atualiza um alerta de idoso vinculado ao cuidador autenticado"
    )
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<AlertasDTO> atualizarAlerta(
            @PathVariable int id,
            @RequestBody AlertasDTO dto,
            Authentication authentication) {
        return ResponseEntity.ok(service.atualizarAlerta(id, dto, extrairCuidadorIdAutenticado(authentication)));
    }

    @Operation(
        summary = "Cancelar alerta",
        description = "Realiza a exclusao logica de um alerta de idoso vinculado ao cuidador autenticado"
    )
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> cancelarAlerta(@PathVariable int id, Authentication authentication) {
        service.cancelarAlerta(id, extrairCuidadorIdAutenticado(authentication));
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Confirmar alerta",
        description = "Marca um alerta do idoso autenticado como realizado"
    )
    @PutMapping("/{id}/confirmar")
    public ResponseEntity<AlertasDTO> confirmarAlerta(@PathVariable int id, Authentication authentication) {
        return ResponseEntity.ok(service.confirmarAlerta(id, extrairUsuarioIdAutenticado(authentication)));
    }

    private Integer extrairCuidadorIdAutenticado(Authentication authentication) {
        return extrairUsuarioIdAutenticado(authentication);
    }

    private Integer extrairUsuarioIdAutenticado(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Integer id) {
            return id;
        }

        if (principal instanceof Number id) {
            return id.intValue();
        }

        return Integer.valueOf(principal.toString());
    }
}
