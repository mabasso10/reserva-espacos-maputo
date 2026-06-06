package com.reservaespacos.repository;

import com.reservaespacos.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    /** Consultar clientes por bairro */
    List<Cliente> findByBairroIgnoreCase(String bairro);

    boolean existsByNumeroDocumento(String numeroDocumento);
}
