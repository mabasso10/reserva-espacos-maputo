package com.reservaespacos.repository;

import com.reservaespacos.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    List<Cliente> findByBairroIgnoreCase(String bairro);
    boolean existsByNumeroDocumento(String numeroDocumento);
    Optional<Cliente> findByUsuarioId(Long usuarioId);
}
