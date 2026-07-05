package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Consulta;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, Integer> {
}
