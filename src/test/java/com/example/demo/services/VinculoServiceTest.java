package com.example.demo.services;

import static com.example.demo.support.TestDataFactory.contato;
import static com.example.demo.support.TestDataFactory.cuidador;
import static com.example.demo.support.TestDataFactory.idoso;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.demo.dtos.ContatoDTO;
import com.example.demo.dtos.VinculoDTO;
import com.example.demo.entity.Cuidador;
import com.example.demo.entity.Idoso;
import com.example.demo.entity.Vinculo;
import com.example.demo.enums.TipoVinculo;
import com.example.demo.exceptions.DuplicateResourceException;
import com.example.demo.exceptions.InvalidRequestException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.repository.CuidadorRepository;
import com.example.demo.repository.IdosoRepository;
import com.example.demo.repository.VinculoRepository;

@ExtendWith(MockitoExtension.class)
class VinculoServiceTest {

    @Mock
    private VinculoRepository repository;

    @Mock
    private IdosoRepository idosoRepository;

    @Mock
    private CuidadorRepository cuidadorRepository;

    @InjectMocks
    private VinculoService service;

    @Test
    void deveListarVinculosQuandoExistiremRegistros() {
        Pageable pageable = PageRequest.of(0, 10);
        Vinculo vinculo = vinculo(1, TipoVinculo.PADRAO);
        Page<Vinculo> pagina = new PageImpl<>(List.of(vinculo), pageable, 1);

        when(repository.findAll(pageable)).thenReturn(pagina);

        Page<VinculoDTO> resultado = service.listarVinculos(pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals(1, resultado.getContent().get(0).getId());
        verify(repository).findAll(pageable);
    }

    @Test
    void deveListarVinculosPorIdosoQuandoExistiremRegistros() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Vinculo> pagina = new PageImpl<>(List.of(vinculo(1, TipoVinculo.PADRAO)), pageable, 1);

        when(repository.findByIdosoId(20, pageable)).thenReturn(pagina);

        Page<VinculoDTO> resultado = service.listarVinculosPorIdoso(20, pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals(20, resultado.getContent().get(0).getIdosoId());
        verify(repository).findByIdosoId(20, pageable);
    }

    @Test
    void deveListarVinculosPorCuidadorQuandoExistiremRegistros() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Vinculo> pagina = new PageImpl<>(List.of(vinculo(1, TipoVinculo.PADRAO)), pageable, 1);

        when(repository.findByCuidadorId(2, pageable)).thenReturn(pagina);

        Page<VinculoDTO> resultado = service.listarVinculosPorCuidador(2, pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals(2, resultado.getContent().get(0).getCuidadorId());
        verify(repository).findByCuidadorId(2, pageable);
    }

    @Test
    void deveBuscarVinculoQuandoIdExistir() {
        when(repository.findById(1)).thenReturn(Optional.of(vinculo(1, TipoVinculo.PADRAO)));

        VinculoDTO resultado = service.buscarVinculoPorId(1);

        assertEquals(1, resultado.getId());
        assertEquals(TipoVinculo.PADRAO, resultado.getTipoVinculo());
    }

    @Test
    void deveCriarVinculoDeEmergenciaQuandoForPrimeiroVinculoDoIdoso() {
        VinculoDTO dto = new VinculoDTO(null, null, 20, 2, null, null, TipoVinculo.PADRAO);
        Vinculo salvo = vinculo(1, TipoVinculo.EMERGENCIA);

        when(repository.existsByIdosoIdAndCuidadorId(20, 2)).thenReturn(false);
        when(repository.existsByIdosoId(20)).thenReturn(false);
        when(idosoRepository.findById(20)).thenReturn(Optional.of(idoso()));
        when(cuidadorRepository.findById(2)).thenReturn(Optional.of(cuidador()));
        when(repository.save(any(Vinculo.class))).thenReturn(salvo);

        VinculoDTO resultado = service.criarVinculo(dto);

        assertEquals(TipoVinculo.EMERGENCIA, resultado.getTipoVinculo());
        verify(repository).save(any(Vinculo.class));
    }

    @Test
    void deveLancarExcecaoQuandoCriarVinculoDuplicado() {
        VinculoDTO dto = new VinculoDTO(null, null, 20, 2, null, null, TipoVinculo.PADRAO);

        when(repository.existsByIdosoIdAndCuidadorId(20, 2)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> service.criarVinculo(dto));
    }

    @Test
    void deveLancarExcecaoQuandoCriarVinculoSemIdosoOuCuidador() {
        VinculoDTO dto = new VinculoDTO();

        assertThrows(InvalidRequestException.class, () -> service.criarVinculo(dto));
    }

    @Test
    void deveDefinirCuidadorEmergenciaQuandoVinculoExistir() {
        Vinculo atual = vinculo(1, TipoVinculo.EMERGENCIA);
        Vinculo novo = vinculo(2, TipoVinculo.PADRAO);

        when(repository.findById(2)).thenReturn(Optional.of(novo));
        when(repository.findByIdosoIdAndTipoVinculo(20, TipoVinculo.EMERGENCIA)).thenReturn(Optional.of(atual));
        when(repository.save(any(Vinculo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        VinculoDTO resultado = service.definirCuidadorEmergencia(2);

        assertEquals(TipoVinculo.PADRAO, atual.getTipoVinculo());
        assertEquals(TipoVinculo.EMERGENCIA, resultado.getTipoVinculo());
        verify(repository).save(atual);
        verify(repository).save(novo);
    }

    @Test
    void deveBuscarContatoDeEmergenciaQuandoCuidadorPossuirContato() {
        Cuidador cuidador = cuidador();
        cuidador.setContato(contato(5, "11", "999999999"));
        Vinculo vinculo = vinculo(1, TipoVinculo.EMERGENCIA);
        vinculo.setCuidador(cuidador);

        when(repository.findByIdosoIdAndTipoVinculo(20, TipoVinculo.EMERGENCIA)).thenReturn(Optional.of(vinculo));

        ContatoDTO resultado = service.buscarContatoDeEmergencia(20);

        assertEquals(5, resultado.getId());
        assertEquals("11", resultado.getDdd());
        assertEquals("999999999", resultado.getTelefone());
    }

    @Test
    void deveExcluirVinculoEPromoverOutroQuandoVinculoForEmergencia() {
        Vinculo emergencia = vinculo(1, TipoVinculo.EMERGENCIA);
        Vinculo outro = vinculo(2, TipoVinculo.PADRAO);

        when(repository.findById(1)).thenReturn(Optional.of(emergencia));
        when(repository.findFirstByIdosoIdAndIdNot(20, 1)).thenReturn(Optional.of(outro));

        service.excluirVinculo(1);

        assertEquals(TipoVinculo.EMERGENCIA, outro.getTipoVinculo());
        verify(repository).save(outro);
        verify(repository).delete(emergencia);
    }

    @Test
    void deveLancarExcecaoQuandoBuscarVinculoInexistente() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.buscarVinculoPorId(99));
    }

    private Vinculo vinculo(int id, TipoVinculo tipoVinculo) {
        Vinculo vinculo = new Vinculo();
        Idoso idoso = idoso(20, "Maria", "12345678901", com.example.demo.enums.Status.ATIVO);
        Cuidador cuidador = cuidador();
        vinculo.setId(id);
        vinculo.setDataCriacao(LocalDate.now());
        vinculo.setIdoso(idoso);
        vinculo.setCuidador(cuidador);
        vinculo.setTipoVinculo(tipoVinculo);
        return vinculo;
    }
}
