package com.example.demo.support;

import java.time.LocalDateTime;
import java.util.Map;

import com.example.demo.dtos.ContatoDTO;
import com.example.demo.dtos.AlertasDTO;
import com.example.demo.dtos.IdosoDTO;
import com.example.demo.dtos.PrescricaoDTO;
import com.example.demo.dtos.RemedioDTO;
import com.example.demo.entity.Administrador;
import com.example.demo.entity.Alertas;
import com.example.demo.entity.Contato;
import com.example.demo.entity.Cuidador;
import com.example.demo.entity.Idoso;
import com.example.demo.entity.Instituicao;
import com.example.demo.entity.Prescricao;
import com.example.demo.entity.Remedio;
import com.example.demo.enums.Perfil;
import com.example.demo.enums.Status;
import com.example.demo.enums.StatusAlertas;
import com.example.demo.enums.TipoAlerta;

public final class TestDataFactory {

    private TestDataFactory() {
    }

    public static RemedioDTO criarRemedioDTO(String nome, String observacao, Status status) {
        RemedioDTO dto = new RemedioDTO();
        dto.setNome(nome);
        dto.setObservacao(observacao);
        dto.setStatus(status);
        return dto;
    }

    public static Remedio criarRemedio(int id, String nome, String observacao, Status status) {
        Remedio remedio = new Remedio();
        remedio.setId(id);
        remedio.setNome(nome);
        remedio.setObservacao(observacao);
        remedio.setStatus(status);
        return remedio;
    }

    public static PrescricaoDTO criarPrescricaoDTO() {
        PrescricaoDTO dto = new PrescricaoDTO();
        dto.setRemedioId(10);
        dto.setIdosoId(20);
        dto.setDosagem("1 comprimido");
        dto.setIntervalo(8.0);
        dto.setNecessarioJejum(false);
        dto.setInstrucao("Tomar com agua");
        dto.setDataFim(LocalDateTime.now().plusDays(7));
        return dto;
    }

    public static Prescricao criarPrescricao(int id, Remedio remedio, Idoso idoso, Status status) {
        Prescricao prescricao = new Prescricao();
        prescricao.setId(id);
        prescricao.setRemedio(remedio);
        prescricao.setIdoso(idoso);
        prescricao.setData_criacao(LocalDateTime.now());
        prescricao.setData_fim(LocalDateTime.now().plusDays(7));
        prescricao.setStatus(status);
        prescricao.setNecessario_jejum(false);
        prescricao.setInstrucao("Tomar com agua");
        prescricao.setIntervalo(8.0);
        prescricao.setDosagem("1 comprimido");
        return prescricao;
    }

    public static AlertasDTO criarAlertaDTO() {
        AlertasDTO dto = new AlertasDTO();
        dto.setIdosoId(20);
        dto.setTipoAlerta(TipoAlerta.REMEDIO);
        dto.setDataAgendada(LocalDateTime.now().plusHours(2));
        dto.setMedico("Dr. Joao");
        dto.setEspecialidade("Cardiologia");
        dto.setLocal("Clinica Bom Cuidado");
        dto.setObservacoes("Levar exames anteriores");
        return dto;
    }

    public static Alertas criarAlerta(int id, Idoso idoso, StatusAlertas statusAlertas) {
        Alertas alerta = new Alertas();
        alerta.setId(id);
        alerta.setIdoso(idoso);
        alerta.setTipoAlerta(TipoAlerta.REMEDIO);
        alerta.setStatusAlertas(statusAlertas);
        alerta.setData_criacao(LocalDateTime.now());
        alerta.setData_agendade(LocalDateTime.now().plusHours(2));
        return alerta;
    }

    public static Remedio criarRemedio() {
        return criarRemedio(10, "Dipirona", null, Status.ATIVO);
    }

    public static IdosoDTO criarIdosoDTO() {
        IdosoDTO dto = new IdosoDTO();
        dto.setNome("Maria");
        dto.setCpf("12345678901");
        dto.setObservacoes("Alergia a dipirona");
        dto.setInstituicaoId(10);
        dto.setContato(criarContatoDTO());
        return dto;
    }

    public static ContatoDTO criarContatoDTO() {
        ContatoDTO dto = new ContatoDTO();
        dto.setDdd("11");
        dto.setTelefone("999999999");
        return dto;
    }

    public static Idoso criarIdoso() {
        Idoso idoso = new Idoso();
        idoso.setId(20);
        idoso.setNome("Maria");
        idoso.setStatus(Status.ATIVO);
        return idoso;
    }

    public static Idoso criarIdoso(int id, String nome, String cpf, Status status) {
        Idoso idoso = criarIdoso();
        idoso.setId(id);
        idoso.setNome(nome);
        idoso.setCpf(cpf);
        idoso.setObservacoes("Alergia a dipirona");
        idoso.setInstituicao(criarInstituicao());
        idoso.setContato(criarContato(5, "11", "999999999"));
        idoso.setData_criacao(LocalDateTime.now());
        idoso.setPerfil(Perfil.IDOSO);
        idoso.setStatus(status);
        return idoso;
    }

    public static Instituicao criarInstituicao() {
        Instituicao instituicao = new Instituicao();
        instituicao.setId(10);
        instituicao.setNome("Instituicao Bom Cuidado");
        instituicao.setStatus(Status.ATIVO);
        return instituicao;
    }

    public static Contato criarContato(Integer id, String ddd, String telefone) {
        Contato contato = new Contato();
        contato.setId(id);
        contato.setDdd(ddd);
        contato.setTelefone(telefone);
        return contato;
    }

    public static Map<String, String> criarDadosLogin(String identificador, String senha, String perfil) {
        return Map.of(
                "identificador", identificador,
                "senha", senha,
                "perfil", perfil);
    }

    public static Administrador criarAdministrador() {
        Administrador administrador = new Administrador();
        administrador.setId(1);
        administrador.setNome("Admin");
        administrador.setCpf("12345678901");
        administrador.setEmail("admin@email.com");
        administrador.setSenha("hash");
        administrador.setPerfil(Perfil.ADMINISTRADOR);
        administrador.setStatus(Status.ATIVO);
        administrador.setData_criacao(LocalDateTime.now());
        return administrador;
    }

    public static Cuidador criarCuidador() {
        Cuidador cuidador = new Cuidador();
        cuidador.setId(2);
        cuidador.setNome("Cuidador");
        cuidador.setCpf("12345678901");
        cuidador.setEmail("cuidador@email.com");
        cuidador.setSenha("hash");
        cuidador.setPerfil(Perfil.CUIDADOR);
        cuidador.setStatus(Status.ATIVO);
        cuidador.setData_criacao(LocalDateTime.now());
        return cuidador;
    }

    public static Instituicao criarInstituicaoAutenticacao() {
        Instituicao instituicao = new Instituicao();
        instituicao.setId(3);
        instituicao.setNome("Instituicao");
        instituicao.setCnpj("12345678000199");
        instituicao.setEmail("instituicao@email.com");
        instituicao.setSenha("hash");
        instituicao.setPerfil(Perfil.INSTITUICAO);
        instituicao.setStatus(Status.ATIVO);
        instituicao.setData_criacao(LocalDateTime.now());
        return instituicao;
    }

    public static RemedioDTO remedioDTO(String nome, String observacao, Status status) {
        return criarRemedioDTO(nome, observacao, status);
    }

    public static Remedio remedio(int id, String nome, String observacao, Status status) {
        return criarRemedio(id, nome, observacao, status);
    }

    public static PrescricaoDTO prescricaoDTO() {
        return criarPrescricaoDTO();
    }

    public static Prescricao prescricao(int id, Remedio remedio, Idoso idoso, Status status) {
        return criarPrescricao(id, remedio, idoso, status);
    }

    public static AlertasDTO alertaDTO() {
        return criarAlertaDTO();
    }

    public static Alertas alerta(int id, Idoso idoso, StatusAlertas statusAlertas) {
        return criarAlerta(id, idoso, statusAlertas);
    }

    public static Remedio remedio() {
        return criarRemedio();
    }

    public static IdosoDTO idosoDTO() {
        return criarIdosoDTO();
    }

    public static ContatoDTO contatoDTO() {
        return criarContatoDTO();
    }

    public static Idoso idoso() {
        return criarIdoso();
    }

    public static Idoso idoso(int id, String nome, String cpf, Status status) {
        return criarIdoso(id, nome, cpf, status);
    }

    public static Instituicao instituicao() {
        return criarInstituicao();
    }

    public static Contato contato(Integer id, String ddd, String telefone) {
        return criarContato(id, ddd, telefone);
    }

    public static Map<String, String> dadosLogin(String identificador, String senha, String perfil) {
        return criarDadosLogin(identificador, senha, perfil);
    }

    public static Administrador administrador() {
        return criarAdministrador();
    }

    public static Cuidador cuidador() {
        return criarCuidador();
    }

    public static Instituicao instituicaoAuth() {
        return criarInstituicaoAutenticacao();
    }

}
