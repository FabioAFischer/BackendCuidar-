package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Cuidador;
import com.example.demo.entity.Instituicao;
import com.example.demo.entity.Remedio;
import com.example.demo.enums.Status;

@Repository
public interface RemedioRepository extends JpaRepository<Remedio, Integer> {

    Optional<Remedio> findById(int id);

    Page<Remedio> findByStatus(Status status, Pageable pageable);

    boolean existsByNome(String nome);

}
