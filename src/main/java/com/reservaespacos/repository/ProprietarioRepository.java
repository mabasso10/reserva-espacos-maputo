package com.reservaespacos.repository;

import com.reservaespacos.model.Proprietario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProprietarioRepository extends JpaRepository<Proprietario, Long> {
    boolean existsByNuit(String nuit);
}
