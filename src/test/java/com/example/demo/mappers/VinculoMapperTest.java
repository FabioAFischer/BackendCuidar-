package com.example.demo.mappers;

import static com.example.demo.support.TestDataFactory.cuidador;
import static com.example.demo.support.TestDataFactory.idoso;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

import com.example.demo.dtos.VinculoDTO;
import com.example.demo.entity.Vinculo;
import com.example.demo.enums.TipoVinculo;

class VinculoMapperTest {

    @Test
    void deveConverterVinculoParaDTOQuandoVinculoForValido() {
        Vinculo vinculo = VinculoMapper.converterDTOParaVinculo(new VinculoDTO(null, null, 20, 2, null, null, TipoVinculo.EMERGENCIA), idoso(), cuidador());
        vinculo.setId(1);

        VinculoDTO dto = VinculoMapper.converterVinculoParaDTO(vinculo);

        assertEquals(1, dto.getId());
        assertEquals(20, dto.getIdosoId());
        assertEquals(2, dto.getCuidadorId());
        assertEquals(TipoVinculo.EMERGENCIA, dto.getTipoVinculo());
    }

    @Test
    void deveConverterDTOParaVinculoQuandoTipoNaoForInformado() {
        Vinculo vinculo = VinculoMapper.converterDTOParaVinculo(new VinculoDTO(), idoso(), cuidador());

        assertNotNull(vinculo.getDataCriacao());
        assertEquals(TipoVinculo.PADRAO, vinculo.getTipoVinculo());
    }

    @Test
    void deveRetornarNuloQuandoVinculoForNulo() {
        assertNull(VinculoMapper.converterVinculoParaDTO(null));
    }
}
