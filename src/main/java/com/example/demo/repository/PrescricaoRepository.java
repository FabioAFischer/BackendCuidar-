package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.entity.Prescricao;
import com.example.demo.enums.Status;

@Repository
public interface PrescricaoRepository extends JpaRepository<Prescricao, Integer> {

    Optional<Prescricao> findById(Integer id);

    Page<Prescricao> findByStatus(Status status, Pageable pageable);

    Page<Prescricao> findByIdoso(Integer idoso, Pageable pageable);

    Page<Prescricao> findByRemedio(Integer remedio, Pageable pageable);

    boolean existsById(Integer id);

}