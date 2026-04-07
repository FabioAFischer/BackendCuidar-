package com.example.demo.services;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.demo.dtos.ContatoDTO;
import com.example.demo.dtos.InstituicaoDTO;
import com.example.demo.entity.Contato;
import com.example.demo.entity.Cuidador;
import com.example.demo.entity.Idoso;
import com.example.demo.entity.Instituicao;
import com.example.demo.enums.Status;
import com.example.demo.mappers.ContatoMapper;
import com.example.demo.mappers.InstituicaoMapper;

import org.springframework.stereotype.Service;

import com.example.demo.repository.ContatoRepository;

@Service
public class ContatoService {
    @Autowired
    private  ContatoRepository contatoRepository;

    //@Autowired
    //private  IdosoRepository idosoRepository;

	public Page<ContatoDTO> listarPorIdoso(Long idosoId, Pageable pageable) {
		return contatoRepository.findByIdosos_Id(idosoId, pageable)
              .map(ContatoMapper::toDTO);
	}

   public ContatoDTO atualizar(Long id, ContatoDTO dto) {
        Contato contato = contatoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contato não encontrado"));

        if (dto.getTelefone() != null) {
            contatoRepository.findByTelefone(dto.getTelefone())
                    .filter(c -> !c.getId().equals(id))
                    .ifPresent(c -> {
                        throw new RuntimeException("Já existe um contato com esse telefone");
                    });
        }

        /*Cuidador cuidador = contato.getCuidador();
        if (dto.getCuidadorId() != null) {
            cuidador = cuidadorRepository.findById(dto.getCuidadorId())
                    .orElseThrow(() -> new RuntimeException("Cuidador não encontrado"));
        }

        List<Idoso> idosos = contato.getIdosos();
        if (dto.getIdososIds() != null) {
            idosos = idosoRepository.findAllById(dto.getIdososIds());
        }

        ContatoMapper.updateEntity(contato, dto, cuidador, idosos);
        */
        Contato atualizado = contatoRepository.save(contato);
        return ContatoMapper.toDTO(atualizado);
    }

   public void inativar(Long id) {
        Contato contato = contatoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contato não encontrado"));

        contatoRepository.delete(contato);
    }

   /* public ContatoDTO criar(ContatoDTO dto) {
        if (contatoRepository.findByTelefone(dto.getTelefone()).isPresent()) {
            throw new RuntimeException("Já existe um contato com esse telefone");
        }

        Cuidador cuidador = null;
        if (dto.getCuidadorId() != null) {
            cuidador = cuidadorRepository.findById(dto.getCuidadorId())
                    .orElseThrow(() -> new RuntimeException("Cuidador não encontrado"));
        }

        List<Idoso> idosos = List.of();
        if (dto.getIdososIds() != null && !dto.getIdososIds().isEmpty()) {
            idosos = idosoRepository.findAllById(dto.getIdososIds());
        }
       
        Contato contato = ContatoMapper.toEntity(dto, cuidador, idosos); 
        Contato salvo = contatoRepository.save(contato);

        return ContatoMapper.toDTO(salvo);
       
    } */



}