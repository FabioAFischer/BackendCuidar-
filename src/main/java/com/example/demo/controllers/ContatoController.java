package com.example.demo.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dtos.ContatoDTO;
import com.example.demo.services.ContatoService;

@RestController
@RequestMapping("/contato")
@CrossOrigin(origins = "*")
public class ContatoController {

  
    private final ContatoService service;
    public ContatoController(ContatoService service) {
        this.service = service;
    }
    

    @GetMapping("/listar_todas/idoso/{idosoId}")
    public ResponseEntity<Page<ContatoDTO>> listarTodas(
            @PathVariable Long idosoId,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(service.listarPorIdoso(idosoId, pageable));
    }


    //@PostMapping("/cadastrar")
    //public ResponseEntity<ContatoDTO> criar(@RequestBody ContatoDTO dto) {
    //    ContatoDTO criada = service.criar(dto);
    //    return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    //}

    /*@PutMapping("/atualizar/{id}")
    public ResponseEntity<ContatoDTO> atualizar(@PathVariable Long id, @RequestBody ContatoDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }*/

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }
}