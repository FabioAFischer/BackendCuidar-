package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.dtos.InstituicaoDTO;
import com.example.demo.dtos.PrescricaoDTO;
import com.example.demo.services.InstituicaoService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/prescricao")
@CrossOrigin(origins = "*")
public class PrescricaoController {

    @Autowired
    private PrescricaoService prescricaoService;

    @Operation(
        summary = "Listar prescrições",
        description = "Retorna uma lista paginada de prescrições ativas ordenadas por nome"
    )
    @GetMapping("/listar_todas")
    public ResponseEntity<Page<PrescricaoDTO>> listarTodas(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listarAtivas(pageable));
    }

    @Operation(
        summary = "Buscar prescrição por ID",
        description = "Retorna os dados de uma prescrição específica com base no ID informado"
    )
    @GetMapping("/listar/{id}")
    public ResponseEntity<PrescricaoDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @Operation(
        summary = "Cadastrar prescrição",
        description = "Cria uma nova prescrição com os dados enviados no corpo da requisição"
    )
    @PostMapping("/cadastrar")
    public ResponseEntity<PrescricaoDTO> criar(@RequestBody PrescricaoDTO dto) {
        PrescricaoDTO criada = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @Operation(
        summary = "Atualizar Prescrição",
        description = "Atualiza os dados de uma prescrição existente com base no ID informado"
    )
    @PutMapping("/atualizar/{id}")
    public ResponseEntity<PrescricaoDTO> atualizar(@PathVariable Integer id, @RequestBody PrescricaoDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @Operation(
        summary = "Deletar (inativar) prescrição",
        description = "Realiza a exclusão lógica (inativação) de uma prescrição com base no ID informado"
    )
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }

}
