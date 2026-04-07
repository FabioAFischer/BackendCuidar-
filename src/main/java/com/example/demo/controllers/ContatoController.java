package com.example.demo.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dtos.ContatoDTO;
import com.example.demo.services.ContatoService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/contato")
@CrossOrigin(origins = "*")
public class ContatoController {

    private final ContatoService service;

    public ContatoController(ContatoService service) {
        this.service = service;
    }

    @Operation(
        summary = "Listar contatos por idoso",
        description = "Retorna uma lista paginada de contatos vinculados a um idoso específico através do ID informado"
    )
    @GetMapping("/listar/{idosoId}")
    public ResponseEntity<Page<ContatoDTO>> listarTodas(
            @PathVariable Long idosoId,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        return ResponseEntity.ok(service.listarPorIdoso(idosoId, pageable));
    }

    /*
    @Operation(
        summary = "Cadastrar contato",
        description = "Cria um novo contato com os dados informados"
    )
    @PostMapping("/cadastrar")
    public ResponseEntity<ContatoDTO> criar(@RequestBody ContatoDTO dto) {
        ContatoDTO criada = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }
    */

    /*
    @Operation(
        summary = "Atualizar contato",
        description = "Atualiza os dados de um contato existente com base no ID informado"
    )
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<ContatoDTO> atualizar(@PathVariable Integer id, @RequestBody ContatoDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }
    */

    @Operation(
        summary = "Deletar (inativar) contato",
        description = "Realiza a exclusão lógica (inativação) de um contato com base no ID informado"
    )
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }
}