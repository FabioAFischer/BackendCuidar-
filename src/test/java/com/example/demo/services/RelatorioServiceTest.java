package com.example.demo.services;

import static com.example.demo.support.TestDataFactory.cuidador;
import static com.example.demo.support.TestDataFactory.idoso;
import static com.example.demo.support.TestDataFactory.instituicaoAuth;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.dtos.RelatorioDTO;
import com.example.demo.dtos.RelatorioDTO.RelatorioInstituicaoDTO;
import com.example.demo.entity.Cuidador;
import com.example.demo.entity.Idoso;
import com.example.demo.entity.Instituicao;
import com.example.demo.enums.Status;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.repository.CuidadorRepository;
import com.example.demo.repository.IdosoRepository;
import com.example.demo.repository.InstituicaoRepository;

@ExtendWith(MockitoExtension.class)
class RelatorioServiceTest {

    @Mock
    private InstituicaoRepository instituicaoRepository;

    @Mock
    private CuidadorRepository cuidadorRepository;

    @Mock
    private IdosoRepository idosoRepository;

    @InjectMocks
    private RelatorioService service;

    @Test
    void deveGerarRelatorioGeralQuandoExistiremDados() {
        Instituicao ativa = instituicao(10, "INSTITUICAO BOM CUIDADO", Status.ATIVO);
        Instituicao inativa = instituicao(11, "OUTRA INSTITUICAO", Status.INATIVO);
        Cuidador cuidadorAtivo = cuidador(Status.ATIVO);
        Idoso idosoInativo = idoso(20, "MARIA", "12345678901", Status.INATIVO);

        when(instituicaoRepository.findAll()).thenReturn(List.of(ativa, inativa));
        when(cuidadorRepository.findAll()).thenReturn(List.of(cuidadorAtivo));
        when(idosoRepository.findAll()).thenReturn(List.of(idosoInativo));

        RelatorioDTO resultado = service.gerarRelatorioGeral();

        assertNotNull(resultado.getGeradoEm());
        assertEquals(2, resultado.getInstituicoes().getTotal());
        assertEquals(1, resultado.getInstituicoes().getAtivas());
        assertEquals(1, resultado.getInstituicoes().getInativas());
        assertEquals("Instituicao Bom Cuidado", resultado.getInstituicoes().getLista().get(0).getNome());
        assertEquals(1, resultado.getCuidadores().getAtivos());
        assertEquals(1, resultado.getIdosos().getInativos());
    }

    @Test
    void deveGerarRelatorioInstituicaoQuandoInstituicaoExistir() {
        Instituicao instituicao = instituicao(10, "INSTITUICAO", Status.ATIVO);
        Cuidador cuidadorAtivo = cuidador(Status.ATIVO);
        Idoso idosoAtivo = idoso(20, "MARIA", "12345678901", Status.ATIVO);

        when(instituicaoRepository.findById(10)).thenReturn(Optional.of(instituicao));
        when(cuidadorRepository.findByInstituicaoId(10)).thenReturn(List.of(cuidadorAtivo));
        when(idosoRepository.findByInstituicaoId(10)).thenReturn(List.of(idosoAtivo));

        RelatorioInstituicaoDTO resultado = service.gerarRelatorioInstituicao(10);

        assertNotNull(resultado.getGeradoEm());
        assertEquals(1, resultado.getCuidadores().getTotal());
        assertEquals(1, resultado.getCuidadores().getAtivos());
        assertEquals(1, resultado.getIdosos().getTotal());
        assertEquals("Maria", resultado.getIdosos().getLista().get(0).getNome());
    }

    @Test
    void deveLancarExcecaoQuandoInstituicaoNaoExistir() {
        when(instituicaoRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.gerarRelatorioInstituicao(99));
    }

    private Instituicao instituicao(int id, String nome, Status status) {
        Instituicao instituicao = instituicaoAuth();
        instituicao.setId(id);
        instituicao.setNome(nome);
        instituicao.setStatus(status);
        instituicao.setRua("RUA DAS FLORES");
        instituicao.setBairro("CENTRO");
        instituicao.setUf("sp");
        return instituicao;
    }

    private Cuidador cuidador(Status status) {
        Cuidador cuidador = com.example.demo.support.TestDataFactory.cuidador();
        cuidador.setStatus(status);
        cuidador.setInstituicao(instituicao(10, "INSTITUICAO", Status.ATIVO));
        return cuidador;
    }
}
