package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Cuidador;
import com.example.demo.enums.Status;

@Repository
public interface CuidadorRepository extends JpaRepository<Cuidador, Integer> {

    Optional<Cuidador> findByCpf(Long cpf);

    boolean existsByCpf(Long cpf);

    Optional<Cuidador> findByLogin(String login);

    boolean existsByLogin(String login);

    Page<Cuidador> findByStatus(Status status, Pageable pageable);

}