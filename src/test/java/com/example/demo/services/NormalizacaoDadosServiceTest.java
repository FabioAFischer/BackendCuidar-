package com.example.demo.services;

import static com.example.demo.support.TestDataFactory.administrador;
import static com.example.demo.support.TestDataFactory.contato;
import static com.example.demo.support.TestDataFactory.cuidador;
import static com.example.demo.support.TestDataFactory.idoso;
import static com.example.demo.support.TestDataFactory.instituicaoAuth;
import static com.example.demo.support.TestDataFactory.prescricao;
import static com.example.demo.support.TestDataFactory.remedio;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.entity.Administrador;
import com.example.demo.entity.Contato;
import com.example.demo.entity.Cuidador;
import com.example.demo.entity.Idoso;
import com.example.demo.entity.Instituicao;
import com.example.demo.entity.Prescricao;
import com.example.demo.entity.Remedio;
import com.example.demo.enums.Status;
import com.example.demo.repository.AdministradorRepository;
import com.example.demo.repository.ContatoRepository;
import com.example.demo.repository.CuidadorRepository;
import com.example.demo.repository.IdosoRepository;
import com.example.demo.repository.InstituicaoRepository;
import com.example.demo.repository.PrescricaoRepository;
import com.example.demo.repository.RemedioRepository;

@ExtendWith(MockitoExtension.class)
class NormalizacaoDadosServiceTest {

    @Mock
    private AdministradorRepository administradorRepository;

    @Mock
    private ContatoRepository contatoRepository;

    @Mock
    private CuidadorRepository cuidadorRepository;

    @Mock
    private IdosoRepository idosoRepository;

    @Mock
    private InstituicaoRepository instituicaoRepository;

    @Mock
    private PrescricaoRepository prescricaoRepository;

    @Mock
    private RemedioRepository remedioRepository;

    @InjectMocks
    private NormalizacaoDadosService service;

    @Test
    void deveNormalizarDadosCadastraisExistentesQuandoHouverRegistros() {
        Administrador administrador = administrador();
        administrador.setNome(" admin ");
        administrador.setCpf("123.456.789-01");
        Contato contato = contato(5, "(11)", "99999-9999");
        Cuidador cuidador = cuidador();
        cuidador.setNome(" cuidador ");
        cuidador.setCpf("123.456.789-01");
        Idoso idoso = idoso(20, " maria ", "123.456.789-01", Status.ATIVO);
        idoso.setObservacoes(" observa ");
        Instituicao instituicao = instituicaoAuth();
        instituicao.setNome(" instituicao ");
        instituicao.setCnpj("12.345.678/0001-99");
        instituicao.setRua(" rua ");
        instituicao.setBairro(" bairro ");
        instituicao.setUf("sp");
        instituicao.setCep("01310-100");
        Prescricao prescricao = prescricao(1, remedio(), idoso, Status.ATIVO);
        prescricao.setInstrucao(" tomar com agua ");
        prescricao.setDosagem(" 1 comprimido ");
        Remedio remedio = remedio(1, " dipirona ", " dor ", Status.ATIVO);

        when(administradorRepository.findAll()).thenReturn(List.of(administrador));
        when(contatoRepository.findAll()).thenReturn(List.of(contato));
        when(cuidadorRepository.findAll()).thenReturn(List.of(cuidador));
        when(idosoRepository.findAll()).thenReturn(List.of(idoso));
        when(instituicaoRepository.findAll()).thenReturn(List.of(instituicao));
        when(prescricaoRepository.findAll()).thenReturn(List.of(prescricao));
        when(remedioRepository.findAll()).thenReturn(List.of(remedio));

        service.normalizarDadosCadastraisExistentes();

        assertEquals("ADMIN", administrador.getNome());
        assertEquals("12345678901", administrador.getCpf());
        assertEquals("11", contato.getDdd());
        assertEquals("CUIDADOR", cuidador.getNome());
        assertEquals("MARIA", idoso.getNome());
        assertEquals("INSTITUICAO", instituicao.getNome());
        assertEquals("1 COMPRIMIDO", prescricao.getDosagem());
        assertEquals("DIPIRONA", remedio.getNome());
        verify(remedioRepository).findAll();
    }
}
