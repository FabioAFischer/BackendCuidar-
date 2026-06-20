package com.example.demo.services;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dtos.PrescricaoDTO;
import com.example.demo.entity.Alertas;
import com.example.demo.entity.Idoso;
import com.example.demo.entity.Prescricao;
import com.example.demo.entity.Remedio;
import com.example.demo.enums.Status;
import com.example.demo.enums.StatusAlertas;
import com.example.demo.enums.TipoAlerta;
import com.example.demo.exceptions.InvalidRequestException;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.mappers.PrescricaoMapper;
import com.example.demo.repository.AlertasRepository;
import com.example.demo.repository.IdosoRepository;
import com.example.demo.repository.PrescricaoRepository;
import com.example.demo.repository.RemedioRepository;

@Service
public class PrescricaoService {

    private static final int MAX_ALERTAS_POR_PRESCRICAO = 10_000;

    private final PrescricaoRepository repository;
    private final RemedioRepository remedioRepository;
    private final IdosoRepository idosoRepository;
    private final AlertasRepository alertasRepository;

    public PrescricaoService(
            PrescricaoRepository repository,
            RemedioRepository remedioRepository,
            IdosoRepository idosoRepository,
            AlertasRepository alertasRepository) {
        this.repository = repository;
        this.remedioRepository = remedioRepository;
        this.idosoRepository = idosoRepository;
        this.alertasRepository = alertasRepository;
    }

    public Page<PrescricaoDTO> listarPrescricoesAtivas(Pageable pageable) {
        return repository.findAtivasNaoVencidas(Status.ATIVO, LocalDateTime.now(), pageable).map(PrescricaoMapper::converterPrescricaoParaDTO);
    }

    public Page<PrescricaoDTO> listarPrescricoesPorIdoso(Integer idosoId, Pageable pageable) {
        return repository.findAtivasNaoVencidasPorIdoso(idosoId, Status.ATIVO, LocalDateTime.now(), pageable).map(PrescricaoMapper::converterPrescricaoParaDTO);
    }

    public PrescricaoDTO buscarPrescricaoPorId(int id) {
        Prescricao prescricao = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescricao", (long) id));
        return PrescricaoMapper.converterPrescricaoParaDTO(prescricao);
    }

    @Transactional
    public PrescricaoDTO criarPrescricao(PrescricaoDTO dto) {
        validarDadosPrescricao(dto);

        Remedio remedio = buscarRemedioPorId(dto.getRemedioId());
        Idoso idoso = buscarIdosoPorId(dto.getIdosoId());
        Prescricao prescricao = repository.save(PrescricaoMapper.converterDTOParaPrescricao(dto, remedio, idoso));

        alertasRepository.saveAll(criarAlertasParaPrescricao(prescricao));

        return PrescricaoMapper.converterPrescricaoParaDTO(prescricao);
    }

    @Transactional
    public PrescricaoDTO atualizarPrescricao(int id, PrescricaoDTO dto) {
        validarDadosPrescricao(dto);

        Prescricao prescricao = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescricao", (long) id));
        Remedio remedio = buscarRemedioPorId(dto.getRemedioId());
        Idoso idoso = buscarIdosoPorId(dto.getIdosoId());

        cancelarAlertasAgendadosDaPrescricao(prescricao);
        PrescricaoMapper.atualizarPrescricaoComDTO(prescricao, dto, remedio, idoso);
        Prescricao prescricaoAtualizada = repository.save(prescricao);

        if (prescricaoAtualizada.getStatus() == Status.ATIVO) {
            alertasRepository.saveAll(criarAlertasParaPrescricao(prescricaoAtualizada, LocalDateTime.now()));
        }

        return PrescricaoMapper.converterPrescricaoParaDTO(prescricaoAtualizada);
    }

    @Transactional
    public void inativarPrescricao(int id) {
        Prescricao prescricao = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescricao", (long) id));

        cancelarAlertasAgendadosDaPrescricao(prescricao);
        prescricao.setStatus(Status.INATIVO);
        repository.save(prescricao);
    }

    private Remedio buscarRemedioPorId(Integer id) {
        return remedioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Remedio", id.longValue()));
    }

    private Idoso buscarIdosoPorId(Integer id) {
        return idosoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Idoso", id.longValue()));
    }

    private void validarDadosPrescricao(PrescricaoDTO dto) {
        if (dto == null) {
            throw new InvalidRequestException("Dados da prescricao nao informados");
        }

        if (dto.getRemedioId() == null) {
            throw new InvalidRequestException("Remedio e obrigatorio");
        }

        if (dto.getIdosoId() == null) {
            throw new InvalidRequestException("Idoso e obrigatorio");
        }

        if (dto.getDosagem() == null || dto.getDosagem().isBlank()) {
            throw new InvalidRequestException("Dosagem e obrigatoria");
        }

        if (dto.getIntervalo() == null || dto.getIntervalo() <= 0) {
            throw new InvalidRequestException("Intervalo deve ser maior que zero");
        }

        if (dto.getDataFim() == null) {
            throw new InvalidRequestException("Data final e obrigatoria");
        }

        if (!dto.getDataFim().isAfter(LocalDateTime.now())) {
            throw new InvalidRequestException("Data final deve ser futura");
        }
    }

    private List<Alertas> criarAlertasParaPrescricao(Prescricao prescricao) {
        return criarAlertasParaPrescricao(prescricao, prescricao.getData_criacao());
    }

    private List<Alertas> criarAlertasParaPrescricao(Prescricao prescricao, LocalDateTime inicioAgenda) {
        long intervaloEmMillis = Math.round(prescricao.getIntervalo() * 60 * 60 * 1000);
        if (intervaloEmMillis <= 0) {
            throw new InvalidRequestException("Intervalo informado e muito pequeno");
        }

        Duration intervalo = Duration.ofMillis(intervaloEmMillis);
        List<Alertas> alertas = new ArrayList<>();
        LocalDateTime dataAgendada = prescricao.getData_criacao();
        int intervalosIgnorados = 0;

        while (dataAgendada.isBefore(inicioAgenda)) {
            if (intervalosIgnorados >= MAX_ALERTAS_POR_PRESCRICAO) {
                throw new InvalidRequestException("Prescricao excede o limite de alertas permitidos");
            }

            dataAgendada = dataAgendada.plus(intervalo);
            intervalosIgnorados++;
        }

        while (!dataAgendada.isAfter(prescricao.getData_fim())) {
            if (alertas.size() >= MAX_ALERTAS_POR_PRESCRICAO) {
                throw new InvalidRequestException("Prescricao excede o limite de alertas permitidos");
            }

            Alertas alerta = new Alertas();
            alerta.setIdoso(prescricao.getIdoso());
            alerta.setPrescricao(prescricao);
            alerta.setTipoAlerta(TipoAlerta.REMEDIO);
            alerta.setStatusAlertas(StatusAlertas.AGENDADO);
            alerta.setData_criacao(prescricao.getData_criacao());
            alerta.setData_agendade(dataAgendada);
            alertas.add(alerta);

            dataAgendada = dataAgendada.plus(intervalo);
        }

        return alertas;
    }

    private void cancelarAlertasAgendadosDaPrescricao(Prescricao prescricao) {
        List<Alertas> alertasAgendados = alertasRepository.findByPrescricaoIdAndStatusAlertas(
                prescricao.getId(),
                StatusAlertas.AGENDADO);

        if (alertasAgendados.isEmpty()) {
            return;
        }

        LocalDateTime agora = LocalDateTime.now();
        alertasAgendados.forEach(alerta -> {
            alerta.setStatusAlertas(StatusAlertas.CANCELADO);
            alerta.setData_atualizacao(agora);
        });
        alertasRepository.saveAll(alertasAgendados);
    }
}
