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

import com.example.demo.dtos.IdosoDTO;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.services.IdosoService;
import java.util.Map;

@RestController
@RequestMapping("/idoso")
@CrossOrigin(origins = "*")
public class IdosoController {

    private final IdosoService service;

    public IdosoController(IdosoService service) {
        this.service = service;
    }

    @GetMapping("/listar_todos")
    public ResponseEntity<Page<IdosoDTO>> listarIdosos(
            Authentication authentication,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        if (verificarAutenticacaoInstituicao(authentication)) {
            Integer instituicaoId = (Integer) authentication.getPrincipal();
            return ResponseEntity.ok(service.listarIdososAtivosPorInstituicao(instituicaoId, pageable));
        }

        return ResponseEntity.ok(service.listarIdososAtivos(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<IdosoDTO> buscarIdosoPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarIdosoPorId(id));
    }

    @GetMapping("/trazerdados/{cpf}")
    public ResponseEntity<IdosoDTO> buscarIdosoPorCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(service.buscarIdosoPorCpf(cpf));
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<IdosoDTO> cadastrarIdoso(@RequestBody IdosoDTO dto) {
        IdosoDTO criada = service.cadastrarIdoso(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<IdosoDTO> atualizarIdoso(@PathVariable Integer id, @RequestBody IdosoDTO dto) {
        return ResponseEntity.ok(service.atualizarIdoso(id, dto));
    }

    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Void> inativarIdoso(@PathVariable Integer id) {
        service.inativarIdoso(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/senha-acesso")
    public ResponseEntity<Map<String, Object>> obterSenhaAcessoDoIdoso(
            @PathVariable Integer id,
            @RequestBody Map<String, String> dados,
            Authentication authentication) {
        if (!verificarAutenticacaoCuidador(authentication)) {
            throw new UnauthorizedException("Apenas cuidadores podem acessar a senha do idoso");
        }

        Integer cuidadorId = (Integer) authentication.getPrincipal();
        return ResponseEntity.ok(service.obterSenhaAcessoDoIdoso(id, cuidadorId, dados.get("senha")));
    }

    private boolean verificarAutenticacaoInstituicao(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_INSTITUICAO".equals(authority.getAuthority()));
    }

    private boolean verificarAutenticacaoCuidador(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_CUIDADOR".equals(authority.getAuthority()));
    }
}
