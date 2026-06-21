package com.example.demo.mappers;

import static com.example.demo.support.TestDataFactory.idoso;
import static com.example.demo.support.TestDataFactory.prescricao;
import static com.example.demo.support.TestDataFactory.prescricaoDTO;
import static com.example.demo.support.TestDataFactory.remedio;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.example.demo.dtos.PrescricaoDTO;
import com.example.demo.entity.Prescricao;
import com.example.demo.enums.Status;

class PrescricaoMapperTest {

    @Test
    void deveConverterPrescricaoParaDTOQuandoPrescricaoForValida() {
        PrescricaoDTO dto = PrescricaoMapper.converterPrescricaoParaDTO(prescricao(1, remedio(), idoso(), Status.ATIVO));

        assertEquals(1, dto.getId());
        assertEquals(10, dto.getRemedioId());
        assertEquals(20, dto.getIdosoId());
        assertEquals("Dipirona", dto.getRemedioNome());
    }

    @Test
    void deveConverterDTOParaPrescricaoQuandoDTOForValido() {
        Prescricao prescricao = PrescricaoMapper.converterDTOParaPrescricao(prescricaoDTO(), remedio(), idoso());

        assertEquals(10, prescricao.getRemedio().getId());
        assertEquals(20, prescricao.getIdoso().getId());
        assertEquals(Status.ATIVO, prescricao.getStatus());
        assertNotNull(prescricao.getData_criacao());
    }

    @Test
    void deveAtualizarPrescricaoQuandoDTOForValido() {
        Prescricao prescricao = prescricao(1, remedio(), idoso(), Status.ATIVO);
        PrescricaoDTO dto = prescricaoDTO();
        dto.setDosagem("2 comprimidos");

        PrescricaoMapper.atualizarPrescricaoComDTO(prescricao, dto, remedio(), idoso());

        assertEquals("2 COMPRIMIDOS", prescricao.getDosagem());
        assertEquals(10, prescricao.getRemedio().getId());
    }

    @Test
    void deveRetornarNuloQuandoPrescricaoForNula() {
        assertNull(PrescricaoMapper.converterPrescricaoParaDTO(null));
    }
}
