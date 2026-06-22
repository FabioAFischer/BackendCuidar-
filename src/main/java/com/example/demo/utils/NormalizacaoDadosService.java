package com.example.demo.utils;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.Administrador;
import com.example.demo.entity.Contato;
import com.example.demo.entity.Cuidador;
import com.example.demo.entity.Idoso;
import com.example.demo.entity.Instituicao;
import com.example.demo.entity.Prescricao;
import com.example.demo.entity.Remedio;
import com.example.demo.repository.AdministradorRepository;
import com.example.demo.repository.ContatoRepository;
import com.example.demo.repository.CuidadorRepository;
import com.example.demo.repository.IdosoRepository;
import com.example.demo.repository.InstituicaoRepository;
import com.example.demo.repository.PrescricaoRepository;
import com.example.demo.repository.RemedioRepository;
@Service
@ConditionalOnProperty(name = "cuidar.normalizacao-dados.enabled", havingValue = "true", matchIfMissing = true)
public class NormalizacaoDadosService {

    private final AdministradorRepository administradorRepository;
    private final ContatoRepository contatoRepository;
    private final CuidadorRepository cuidadorRepository;
    private final IdosoRepository idosoRepository;
    private final InstituicaoRepository instituicaoRepository;
    private final PrescricaoRepository prescricaoRepository;
    private final RemedioRepository remedioRepository;

    public NormalizacaoDadosService(
            AdministradorRepository administradorRepository,
            ContatoRepository contatoRepository,
            CuidadorRepository cuidadorRepository,
            IdosoRepository idosoRepository,
            InstituicaoRepository instituicaoRepository,
            PrescricaoRepository prescricaoRepository,
            RemedioRepository remedioRepository) {
        this.administradorRepository = administradorRepository;
        this.contatoRepository = contatoRepository;
        this.cuidadorRepository = cuidadorRepository;
        this.idosoRepository = idosoRepository;
        this.instituicaoRepository = instituicaoRepository;
        this.prescricaoRepository = prescricaoRepository;
        this.remedioRepository = remedioRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void normalizarDadosCadastraisExistentes() {
        normalizarAdministradores();
        normalizarContatos();
        normalizarCuidadores();
        normalizarIdosos();
        normalizarInstituicoes();
        normalizarPrescricoes();
        normalizarRemedios();
    }

    private void normalizarAdministradores() {
        for (Administrador administrador : administradorRepository.findAll()) {
            administrador.setNome(TextoUtils.normalizarTextoParaBanco(administrador.getNome()));
            administrador.setCpf(TextoUtils.normalizarDocumento(administrador.getCpf()));
        }
    }

    private void normalizarContatos() {
        for (Contato contato : contatoRepository.findAll()) {
            contato.setDdd(TextoUtils.normalizarNumero(contato.getDdd()));
            contato.setTelefone(TextoUtils.normalizarNumero(contato.getTelefone()));
        }
    }

    private void normalizarCuidadores() {
        for (Cuidador cuidador : cuidadorRepository.findAll()) {
            cuidador.setNome(TextoUtils.normalizarTextoParaBanco(cuidador.getNome()));
            cuidador.setCpf(TextoUtils.normalizarDocumento(cuidador.getCpf()));
        }
    }

    private void normalizarIdosos() {
        for (Idoso idoso : idosoRepository.findAll()) {
            idoso.setNome(TextoUtils.normalizarTextoParaBanco(idoso.getNome()));
            idoso.setCpf(TextoUtils.normalizarDocumento(idoso.getCpf()));
        }
    }

    private void normalizarInstituicoes() {
        for (Instituicao instituicao : instituicaoRepository.findAll()) {
            instituicao.setNome(TextoUtils.normalizarTextoParaBanco(instituicao.getNome()));
            instituicao.setCnpj(TextoUtils.normalizarDocumento(instituicao.getCnpj()));
            instituicao.setRua(TextoUtils.normalizarTextoParaBanco(instituicao.getRua()));
            instituicao.setBairro(TextoUtils.normalizarTextoParaBanco(instituicao.getBairro()));
            instituicao.setUf(TextoUtils.normalizarTextoParaBanco(instituicao.getUf()));
            instituicao.setCep(TextoUtils.normalizarDocumento(instituicao.getCep()));
        }
    }

    private void normalizarPrescricoes() {
        for (Prescricao prescricao : prescricaoRepository.findAll()) {
            prescricao.setDosagem(TextoUtils.normalizarTextoParaBanco(prescricao.getDosagem()));
        }
    }

    private void normalizarRemedios() {
        for (Remedio remedio : remedioRepository.findAll()) {
            remedio.setNome(TextoUtils.normalizarTextoParaBanco(remedio.getNome()));
        }
    }
}
