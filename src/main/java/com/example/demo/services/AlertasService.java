package com.example.demo.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.dtos.AlertasDTO;
import com.example.demo.entity.Alertas;
import com.example.demo.entity.Idoso;
import com.example.demo.entity.Prescricao;
import com.example.demo.enums.StatusAlertas;
import com.example.demo.enums.TipoAlerta;
import com.example.demo.exceptions.InvalidRequestException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.exceptions.UnauthorizedException;
import com.example.demo.mappers.AlertasMapper;
import com.example.demo.repository.AlertasRepository;
import com.example.demo.repository.IdosoRepository;
import com.example.demo.repository.PrescricaoRepository;
import com.example.demo.repository.VinculoRepository;

@Service
public class AlertasService {

    private final AlertasRepository repository;
    private final IdosoRepository idosoRepository;
    private final PrescricaoRepository prescricaoRepository;
    private final VinculoRepository vinculoRepository;

    public AlertasService(
            AlertasRepository repository,
            IdosoRepository idosoRepository,
            PrescricaoRepository prescricaoRepository,
            VinculoRepository vinculoRepository) {
        this.repository = repository;
        this.idosoRepository = idosoRepository;
        this.prescricaoRepository = prescricaoRepository;
        this.vinculoRepository = vinculoRepository;
    }

    public Page<AlertasDTO> listarAlertasAtivosDoCuidador(Integer cuidadorId, Pageable pageable) {
        validarCuidadorAutenticado(cuidadorId);
        return repository.findNaoCanceladosPorCuidador(cuidadorId, StatusAlertas.CANCELADO, pageable)
                .map(AlertasMapper::converterAlertaParaDTO);
    }

    public Page<AlertasDTO> listarAlertasPorIdoso(Integer idosoId, Integer cuidadorId, Pageable pageable) {
        validarCuidadorAutenticado(cuidadorId);
        validarVinculoEntreIdosoECuidador(idosoId, cuidadorId);
        return repository.findNaoCanceladosPorIdosoECuidador(idosoId, cuidadorId, StatusAlertas.CANCELADO, pageable)
                .map(AlertasMapper::converterAlertaParaDTO);
    }

    public Page<AlertasDTO> listarAlertasDoIdoso(Integer idosoId, Pageable pageable) {
        validarIdosoAutenticado(idosoId);
        return repository.findByIdosoIdAndStatusAlertasNot(idosoId, StatusAlertas.CANCELADO, pageable)
                .map(AlertasMapper::converterAlertaParaDTO);
    }

    public AlertasDTO buscarAlertaPorId(int id, Integer cuidadorId) {
        validarCuidadorAutenticado(cuidadorId);
        Alertas alerta = buscarEntidadeAlertaPorId(id);
        validarVinculoEntreIdosoECuidador(alerta.getIdoso().getId(), cuidadorId);
        return AlertasMapper.converterAlertaParaDTO(alerta);
    }

    public AlertasDTO criarAlerta(AlertasDTO dto, Integer cuidadorId) {
        validarCuidadorAutenticado(cuidadorId);
        validarDadosAlerta(dto);

        Idoso idoso = buscarIdosoPorId(dto.getIdosoId());
        validarVinculoEntreIdosoECuidador(idoso.getId(), cuidadorId);
        Prescricao prescricao = buscarPrescricaoVinculadaAoAlerta(dto, idoso);

        return AlertasMapper.converterAlertaParaDTO(repository.save(AlertasMapper.converterDTOParaAlerta(dto, idoso, prescricao)));
    }

    public AlertasDTO atualizarAlerta(int id, AlertasDTO dto, Integer cuidadorId) {
        validarCuidadorAutenticado(cuidadorId);
        validarDadosAlerta(dto);

        Alertas alerta = buscarEntidadeAlertaPorId(id);
        validarVinculoEntreIdosoECuidador(alerta.getIdoso().getId(), cuidadorId);

        Idoso idoso = buscarIdosoPorId(dto.getIdosoId());
        validarVinculoEntreIdosoECuidador(idoso.getId(), cuidadorId);
        Prescricao prescricao = buscarPrescricaoVinculadaAoAlerta(dto, idoso, alerta.getPrescricao());

        AlertasMapper.atualizarAlertaComDTO(alerta, dto, idoso, prescricao);
        return AlertasMapper.converterAlertaParaDTO(repository.save(alerta));
    }

    public void cancelarAlerta(int id, Integer cuidadorId) {
        validarCuidadorAutenticado(cuidadorId);

        Alertas alerta = buscarEntidadeAlertaPorId(id);
        validarVinculoEntreIdosoECuidador(alerta.getIdoso().getId(), cuidadorId);

        alerta.setStatusAlertas(StatusAlertas.CANCELADO);
        alerta.setData_atualizacao(LocalDateTime.now());
        repository.save(alerta);
    }

    public AlertasDTO confirmarAlerta(int id, Integer idosoId) {
        validarIdosoAutenticado(idosoId);

        Alertas alerta = buscarEntidadeAlertaPorId(id);
        if (alerta.getIdoso() == null || alerta.getIdoso().getId() != idosoId) {
            throw new UnauthorizedException("Alerta nao pertence ao idoso autenticado");
        }

        if (alerta.getStatusAlertas() == StatusAlertas.CANCELADO) {
            throw new InvalidRequestException("Alerta cancelado nao pode ser confirmado");
        }

        alerta.setStatusAlertas(StatusAlertas.REALIZADO);
        alerta.setData_atualizacao(LocalDateTime.now());
        return AlertasMapper.converterAlertaParaDTO(repository.save(alerta));
    }

    private Alertas buscarEntidadeAlertaPorId(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alerta", (long) id));
    }

    private Idoso buscarIdosoPorId(Integer id) {
        return idosoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Idoso", id.longValue()));
    }

    private Prescricao buscarPrescricaoVinculadaAoAlerta(AlertasDTO dto, Idoso idoso) {
        return buscarPrescricaoVinculadaAoAlerta(dto, idoso, null);
    }

    private Prescricao buscarPrescricaoVinculadaAoAlerta(AlertasDTO dto, Idoso idoso, Prescricao prescricaoAtual) {
        if (dto.getTipoAlerta() != TipoAlerta.REMEDIO) {
            return null;
        }

        if (dto.getPrescricaoId() == null) {
            if (prescricaoAtual == null) {
                throw new InvalidRequestException("Prescricao e obrigatoria para alerta de remedio");
            }

            validarPrescricaoPertenceAoIdoso(prescricaoAtual, idoso);
            return prescricaoAtual;
        }

        Prescricao prescricao = prescricaoRepository.findById(dto.getPrescricaoId())
                .orElseThrow(() -> new ResourceNotFoundException("Prescricao", dto.getPrescricaoId().longValue()));

        validarPrescricaoPertenceAoIdoso(prescricao, idoso);
        return prescricao;
    }

    private void validarPrescricaoPertenceAoIdoso(Prescricao prescricao, Idoso idoso) {
        if (prescricao.getIdoso() == null || prescricao.getIdoso().getId() != idoso.getId()) {
            throw new InvalidRequestException("Prescricao nao pertence ao idoso informado");
        }
    }

    private void validarDadosAlerta(AlertasDTO dto) {
        if (dto == null) {
            throw new InvalidRequestException("Dados do alerta nao informados");
        }

        if (dto.getIdosoId() == null) {
            throw new InvalidRequestException("Idoso e obrigatorio");
        }

        if (dto.getTipoAlerta() == null) {
            throw new InvalidRequestException("Tipo do alerta e obrigatorio");
        }

        if (dto.getDataAgendada() == null) {
            throw new InvalidRequestException("Data agendada e obrigatoria");
        }
    }

    private void validarCuidadorAutenticado(Integer cuidadorId) {
        if (cuidadorId == null) {
            throw new UnauthorizedException("Cuidador autenticado nao identificado");
        }
    }

    private void validarIdosoAutenticado(Integer idosoId) {
        if (idosoId == null) {
            throw new UnauthorizedException("Idoso autenticado nao identificado");
        }
    }

    private void validarVinculoEntreIdosoECuidador(Integer idosoId, Integer cuidadorId) {
        if (!vinculoRepository.existsByIdosoIdAndCuidadorId(idosoId, cuidadorId)) {
            throw new UnauthorizedException("Cuidador nao possui vinculo com o idoso informado");
        }
    }
}
