package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Idoso;
import com.example.demo.enums.Status;

@Repository
public interface IdosoRepository extends JpaRepository<Idoso, Long> {

    Optional<Idoso> findByCpf(Long cpf);

    boolean existsByCpf(Long cpf);

    Page<Idoso> findByStatus(Status status, Pageable pageable);

}
