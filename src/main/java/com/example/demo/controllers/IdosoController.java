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

import com.example.demo.dtos.IdosoDTO;
import com.example.demo.services.IdosoService;

@RestController
@RequestMapping("/idoso")
@CrossOrigin(origins = "*")
public class IdosoController {

    private final IdosoService service;

    public IdosoController(IdosoService service) {
        this.service = service;
    }

    @GetMapping("/listar_todos")
    public ResponseEntity<Page<IdosoDTO>> listarTodos(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(service.listarAtivos(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IdosoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<IdosoDTO> criar(@RequestBody IdosoDTO dto) {
        IdosoDTO criada = service.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<IdosoDTO> atualizar(@PathVariable Long id, @RequestBody IdosoDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
