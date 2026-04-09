package com.example.demo.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dtos.InstituicaoDTO;
import com.example.demo.services.InstituicaoService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/instituicao")
@CrossOrigin(origins = "*")
public class InstituicaoController {

    private final InstituicaoService service;

    public InstituicaoController(InstituicaoService service) {
        this.service = service;
    }

    @Operation(
        summary = "Listar instituições",
        description = "Retorna uma lista paginada de instituições ativas ordenadas por nome"
    )
    @GetMapping("/listar_todas")
    public ResponseEntity<Page<InstituicaoDTO>> listarTodas(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listarAtivas(pageable));
    }

    @Operation(
        summary = "Buscar instituição por ID",
        description = "Retorna os dados de uma instituição específica com base no ID informado"
    )
    @GetMapping("/listar/{id}")
    public ResponseEntity<InstituicaoDTO> buscarPorId(@PathVariable int id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(
        summary = "Cadastrar instituição",
        description = "Cria uma nova instituição com os dados enviados no corpo da requisição"
    )
    @PostMapping("/cadastrar")
    public ResponseEntity<InstituicaoDTO> criar(@RequestBody InstituicaoDTO dto) {
        InstituicaoDTO criada = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @Operation(
        summary = "Atualizar instituição",
        description = "Atualiza os dados de uma instituição existente com base no ID informado"
    )
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<InstituicaoDTO> atualizar(@PathVariable Integer id, @RequestBody InstituicaoDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @Operation(
        summary = "Deletar (inativar) instituição",
        description = "Realiza a exclusão lógica (inativação) de uma instituição com base no ID informado"
    )
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }
}