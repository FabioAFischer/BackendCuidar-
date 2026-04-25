package com.example.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Alerta;
import com.example.demo.enums.StatusAlertas;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Integer> {

    Page<Alerta> findByStatusAlertas(StatusAlertas statusAlertas, Pageable pageable);
}
