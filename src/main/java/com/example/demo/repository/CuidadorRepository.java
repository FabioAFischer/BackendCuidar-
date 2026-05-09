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

    Optional<Cuidador> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    Page<Cuidador> findByStatus(Status status, Pageable pageable);

<<<<<<< HEAD
    Page<Cuidador> findByInstituicaoIdAndStatus(Integer instituicaoId, Status status, Pageable pageable);
=======
    Page<Cuidador> findByStatusAndInstituicaoId(Status status, Integer instituicaoId, Pageable pageable);

    Page<Cuidador> findByStatusAndInstituicaoIdAndCpf(Status status, Integer instituicaoId, String cpf, Pageable pageable);
>>>>>>> 350e333 (Filtra cuidadores por instituicao e CPF)

    Optional<Cuidador> findByEmail(String email);

}
