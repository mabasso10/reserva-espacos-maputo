package com.reservaespacos.service;

import com.reservaespacos.exception.RecursoNaoEncontradoException;
import com.reservaespacos.exception.RegraDeNegocioException;
import com.reservaespacos.model.Proprietario;
import com.reservaespacos.repository.ProprietarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProprietarioService {

    @Autowired
    private ProprietarioRepository proprietarioRepository;

    public Proprietario registar(Proprietario proprietario) {
        if (proprietarioRepository.existsByNuit(proprietario.getNuit())) {
            throw new RegraDeNegocioException(
                "Ja existe um proprietario com o NUIT: " + proprietario.getNuit()
            );
        }
        return proprietarioRepository.save(proprietario);
    }

    public Proprietario actualizar(Long id, Proprietario dadosNovos) {
        Proprietario proprietario = buscarPorId(id);
        proprietario.setNome(dadosNovos.getNome());
        proprietario.setApelido(dadosNovos.getApelido());
        proprietario.setTelefone(dadosNovos.getTelefone());
        proprietario.setBairro(dadosNovos.getBairro());
        if (!proprietario.getNuit().equals(dadosNovos.getNuit())) {
            if (proprietarioRepository.existsByNuit(dadosNovos.getNuit())) {
                throw new RegraDeNegocioException("NUIT ja em uso: " + dadosNovos.getNuit());
            }
            proprietario.setNuit(dadosNovos.getNuit());
        }
        // Preservar a ligação ao usuario — nunca deixar nula por uma edição
        if (dadosNovos.getUsuario() != null) {
            proprietario.setUsuario(dadosNovos.getUsuario());
        }
        return proprietarioRepository.save(proprietario);
    }

    public void remover(Long id) {
        Proprietario proprietario = buscarPorId(id);
        proprietarioRepository.delete(proprietario);
    }

    @Transactional(readOnly = true)
    public List<Proprietario> listarTodos() {
        return proprietarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Proprietario buscarPorId(Long id) {
        return proprietarioRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Proprietario nao encontrado com ID: " + id));
    }

    /** Lookup fiável por usuario_id — usado em /proprietario/me */
    @Transactional(readOnly = true)
    public Optional<Proprietario> buscarPorUsuarioId(Long usuarioId) {
        return proprietarioRepository.findByUsuarioId(usuarioId);
    }
}
