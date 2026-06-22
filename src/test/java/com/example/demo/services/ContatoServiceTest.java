package com.example.demo.services;

import static com.example.demo.support.TestDataFactory.contato;
import static com.example.demo.support.TestDataFactory.cuidador;
import static com.example.demo.support.TestDataFactory.idoso;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.example.demo.dtos.ContatoDTO;
import com.example.demo.entity.Contato;
import com.example.demo.entity.Idoso;
import com.example.demo.exceptions.ResourceInUseException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.repository.ContatoRepository;
import com.example.demo.repository.CuidadorRepository;
import com.example.demo.repository.IdosoRepository;

@ExtendWith(MockitoExtension.class)
class ContatoServiceTest {

    @Mock
    private ContatoRepository contatoRepository;

    @Mock
    private CuidadorRepository cuidadorRepository;

    @Mock
    private IdosoRepository idosoRepository;

    @InjectMocks
    private ContatoService service;

    @Test
    void deveListarContatosQuandoExistiremRegistros() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Contato> pagina = new PageImpl<>(List.of(contato(5, "11", "999999999")), pageable, 1);

        when(contatoRepository.findAll(pageable)).thenReturn(pagina);

        Page<ContatoDTO> resultado = service.listarContatos(pageable);

        assertEquals(1, resultado.getTotalElements());
        assertEquals("999999999", resultado.getContent().get(0).getTelefone());
    }

    @Test
    void deveListarContatosPorIdosoQuandoExistiremRegistros() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Contato> pagina = new PageImpl<>(List.of(contato(5, "11", "999999999")), pageable, 1);

        when(contatoRepository.findByIdosos_Id(20, pageable)).thenReturn(pagina);

        Page<ContatoDTO> resultado = service.listarContatosPorIdoso(20, pageable);

        assertEquals(1, resultado.getTotalElements());
        verify(contatoRepository).findByIdosos_Id(20, pageable);
    }

    @Test
    void deveBuscarContatoQuandoIdExistir() {
        when(contatoRepository.findById(5)).thenReturn(Optional.of(contato(5, "11", "999999999")));

        ContatoDTO resultado = service.buscarContatoPorId(5);

        assertEquals(5, resultado.getId());
        assertEquals("11", resultado.getDdd());
    }

    @Test
    void deveCriarContatoQuandoDadosForemValidos() {
        ContatoDTO dto = contatoDTO(2, List.of(20));
        Idoso idoso = idoso();
        Contato salvo = contato(5, "11", "999999999");

        when(cuidadorRepository.findById(2)).thenReturn(Optional.of(cuidador()));
        when(idosoRepository.findAllById(List.of(20))).thenReturn(List.of(idoso));
        when(contatoRepository.save(any(Contato.class))).thenReturn(salvo);

        ContatoDTO resultado = service.criarContato(dto);

        assertEquals(5, resultado.getId());
        verify(idosoRepository).saveAll(List.of(idoso));
    }

    @Test
    void deveAtualizarContatoQuandoContatoExistir() {
        ContatoDTO dto = contatoDTO(null, List.of(20));
        Contato existente = contato(5, "11", "999999999");
        Idoso idoso = idoso();

        when(contatoRepository.findById(5)).thenReturn(Optional.of(existente));
        when(idosoRepository.findAllById(List.of(20))).thenReturn(List.of(idoso));
        when(contatoRepository.save(existente)).thenReturn(existente);

        ContatoDTO resultado = service.atualizarContato(5, dto);

        assertEquals("21", resultado.getDdd());
        assertEquals("988887777", resultado.getTelefone());
        verify(idosoRepository).saveAll(List.of(idoso));
    }

    @Test
    void deveExcluirContatoQuandoNaoPossuirVinculos() {
        Contato contato = contato(5, "11", "999999999");
        contato.setIdosos(List.of());

        when(contatoRepository.findById(5)).thenReturn(Optional.of(contato));

        service.excluirContato(5);

        verify(contatoRepository).delete(contato);
    }

    @Test
    void deveLancarExcecaoQuandoExcluirContatoEmUso() {
        Contato contato = contato(5, "11", "999999999");
        contato.setCuidador(cuidador());

        when(contatoRepository.findById(5)).thenReturn(Optional.of(contato));

        assertThrows(ResourceInUseException.class, () -> service.excluirContato(5));
    }

    @Test
    void deveLancarExcecaoQuandoContatoNaoExistir() {
        when(contatoRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.buscarContatoPorId(99));
    }

    @Test
    void deveLancarExcecaoQuandoAlgumIdosoNaoExistir() {
        ContatoDTO dto = contatoDTO(null, List.of(20, 21));

        when(idosoRepository.findAllById(List.of(20, 21))).thenReturn(List.of(idoso()));

        assertThrows(ResourceNotFoundException.class, () -> service.criarContato(dto));
    }

    private ContatoDTO contatoDTO(Integer cuidadorId, List<Integer> idosos) {
        ContatoDTO dto = new ContatoDTO();
        dto.setDdd("21");
        dto.setTelefone("988887777");
        dto.setCuidadorId(cuidadorId);
        dto.setIdosos(idosos);
        return dto;
    }
}
