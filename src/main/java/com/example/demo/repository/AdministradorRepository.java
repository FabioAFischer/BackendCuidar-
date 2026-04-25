package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Administrador;
import com.example.demo.enums.Status;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {

    Optional<Administrador> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<Administrador> findByStatus(Status status, Pageable pageable);
}
