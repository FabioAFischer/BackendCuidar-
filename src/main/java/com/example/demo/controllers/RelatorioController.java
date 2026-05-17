package com.example.demo.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dtos.RelatorioDTO;
import com.example.demo.services.RelatorioService;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class RelatorioController {

    private final RelatorioService service;

    public RelatorioController(RelatorioService service) {
        this.service = service;
    }

    @Operation(
        summary = "Gerar relatório",
        description = "Retorna dados consolidados de instituições, cuidadores e idosos para geração de relatório PDF"
    )
    @GetMapping("/relatorio")
    public ResponseEntity<RelatorioDTO> gerar() {
        return ResponseEntity.ok(service.gerar());
    }
}