package com.example.demo.mappers;

import static com.example.demo.support.TestDataFactory.remedio;
import static com.example.demo.support.TestDataFactory.remedioDTO;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.example.demo.dtos.RemedioDTO;
import com.example.demo.entity.Remedio;
import com.example.demo.enums.Status;

class RemedioMapperTest {

    @Test
    void deveConverterRemedioParaDTOQuandoRemedioForValido() {
        RemedioDTO dto = RemedioMapper.converterRemedioParaDTO(remedio(1, "DIPIRONA", "Tomar COM água", Status.ATIVO));

        assertEquals(1, dto.getId());
        assertEquals("Dipirona", dto.getNome());
        assertEquals("Tomar COM água", dto.getObservacao());
    }

    @Test
    void deveConverterDTOParaRemedioQuandoDTOForValido() {
        Remedio remedio = RemedioMapper.converterDTOParaRemedio(remedioDTO("Dipirona", "Tomar", null));

        assertEquals("DIPIRONA", remedio.getNome());
        assertEquals("Tomar", remedio.getObservacao());
        assertEquals(Status.ATIVO, remedio.getStatus());
    }

    @Test
    void deveAtualizarRemedioQuandoDTOForValido() {
        Remedio remedio = remedio(1, "DIPIRONA", null, Status.ATIVO);

        RemedioMapper.atualizarRemedioComDTO(remedio, remedioDTO("Paracetamol", "Nova", Status.INATIVO));

        assertEquals("PARACETAMOL", remedio.getNome());
        assertEquals("Nova", remedio.getObservacao());
        assertEquals(Status.INATIVO, remedio.getStatus());
    }

    @Test
    void deveRetornarNuloQuandoRemedioForNulo() {
        assertNull(RemedioMapper.converterRemedioParaDTO(null));
    }
}
