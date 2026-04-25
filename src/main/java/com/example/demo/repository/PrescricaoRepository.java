package com.example.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.entity.Prescricao;
import com.example.demo.enums.Status;

@Repository
public interface PrescricaoRepository extends JpaRepository<Prescricao, Integer> {

    Page<Prescricao> findByStatus(Status status, Pageable pageable);

    Page<Prescricao> findByIdoso_Id(Integer idosoId, Pageable pageable);

    Page<Prescricao> findByRemedio_Id(Integer remedioId, Pageable pageable);

}
