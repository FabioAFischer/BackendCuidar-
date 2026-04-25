package com.example.demo.services;


import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.stereotype.Service;

import com.example.demo.repository.ContatoRepository;

@Service
public class ContatoService {
    @Autowired
    private  ContatoRepository contatoRepository;

    //@Autowired
    //private  IdosoRepository idosoRepository;

	/*public Page<ContatoDTO> listarPorIdoso(Integer idosoId, Pageable pageable) {
		return contatoRepository.findByIdosos_Id(idosoId, pageable)
              .map(ContatoMapper::toDTO);
	}

  public ContatoDTO atualizar(Integer id, ContatoDTO dto) {
        Contato contato = contatoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contato não encontrado"));

        if (dto.getTelefone() != null) {
            contatoRepository.findByTelefone(dto.getTelefone())
                    .filter(c -> !c.getId().equals(id))
                    .ifPresent(c -> {
                        throw new RuntimeException("Já existe um contato com esse telefone");
                    });
        }

        Cuidador cuidador = contato.getCuidador();
        if (dto.getCuidadorId() != null) {
            cuidador = cuidadorRepository.findById(dto.getCuidadorId())
                    .orElseThrow(() -> new RuntimeException("Cuidador não encontrado"));
        }

        List<Idoso> idosos = contato.getIdosos();
        if (dto.getIdososIds() != null) {
            idosos = idosoRepository.findAllById(dto.getIdososIds());
        }

        ContatoMapper.updateEntity(contato, dto, cuidador, idosos);
        
        Contato atualizado = contatoRepository.save(contato);
        return ContatoMapper.toDTO(atualizado);
    }

   public void inativar(Integer id) {
        Contato contato = contatoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contato não encontrado"));

        contatoRepository.delete(contato);
    }

    public ContatoDTO criar(ContatoDTO dto) {
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