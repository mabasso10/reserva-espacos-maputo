package com.reservaespacos.repository;

import com.reservaespacos.model.Proprietario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProprietarioRepository extends JpaRepository<Proprietario, Long> {
    boolean existsByNuit(String nuit);
    Optional<Proprietario> findByUsuarioId(Long usuarioId);
}
