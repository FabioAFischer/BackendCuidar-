package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Instituicao;

@Repository
public interface InstituicaoRepository extends JpaRepository<Instituicao, Long> {

    Optional<Instituicao> findByCnpj(String cnpj);

    boolean existsByCnpj(String cnpj);
}