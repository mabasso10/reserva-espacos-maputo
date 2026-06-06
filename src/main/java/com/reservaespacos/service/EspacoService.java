package com.reservaespacos.service;

import com.reservaespacos.dto.Dtos;
import com.reservaespacos.exception.RecursoNaoEncontradoException;
import com.reservaespacos.model.Espaco;
import com.reservaespacos.model.Proprietario;
import com.reservaespacos.repository.EspacoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EspacoService {

    @Autowired private EspacoRepository espacoRepository;
    @Autowired private ProprietarioService proprietarioService;

    /** Registar espaco a partir do DTO */
    public Espaco registar(Dtos.EspacoRequest dto) {
        Proprietario proprietario = proprietarioService.buscarPorId(dto.getProprietarioId());
        Espaco espaco = new Espaco();
        espaco.setProprietario(proprietario);
        espaco.setNomeEspaco(dto.getNomeEspaco());
        espaco.setDescricao(dto.getDescricao());
        espaco.setBairro(dto.getBairro());
        espaco.setCapacidade(dto.getCapacidade());
        espaco.setPrecoReserva(dto.getPrecoReserva());
        espaco.setTipoEvento(dto.getTipoEvento());
        espaco.setFoto(dto.getFoto());
        espaco.setDisponibilidade(dto.getDisponibilidade() != null ? dto.getDisponibilidade() : true);
        return espacoRepository.save(espaco);
    }

    /** Actualizar espaco a partir do DTO */
    public Espaco actualizar(Long id, Dtos.EspacoRequest dto) {
        Espaco espaco = buscarPorId(id);
        Proprietario proprietario = proprietarioService.buscarPorId(dto.getProprietarioId());
        espaco.setProprietario(proprietario);
        espaco.setNomeEspaco(dto.getNomeEspaco());
        espaco.setDescricao(dto.getDescricao());
        espaco.setBairro(dto.getBairro());
        espaco.setCapacidade(dto.getCapacidade());
        espaco.setPrecoReserva(dto.getPrecoReserva());
        espaco.setTipoEvento(dto.getTipoEvento());
        if (dto.getFoto() != null) espaco.setFoto(dto.getFoto());
        espaco.setDisponibilidade(dto.getDisponibilidade() != null ? dto.getDisponibilidade() : espaco.getDisponibilidade());
        return espacoRepository.save(espaco);
    }

    /** Actualizar apenas a foto (usado pelo endpoint de upload) */
    public Espaco actualizarFoto(Long id, String nomeFicheiro) {
        Espaco espaco = buscarPorId(id);
        espaco.setFoto(nomeFicheiro);
        return espacoRepository.save(espaco);
    }

    /** Remover espaco */
    public void remover(Long id) {
        Espaco espaco = buscarPorId(id);
        espacoRepository.delete(espaco);
    }

    /** Listar todos */
    @Transactional(readOnly = true)
    public List<Espaco> listarTodos() {
        return espacoRepository.findAll();
    }

    /** Consultar espaco por ID */
    @Transactional(readOnly = true)
    public Espaco buscarPorId(Long id) {
        return espacoRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Espaco nao encontrado com ID: " + id));
    }

    /** Consultar espacos por proprietario */
    @Transactional(readOnly = true)
    public List<Espaco> buscarPorProprietario(Long proprietarioId) {
        List<Espaco> lista = espacoRepository.findByProprietarioId(proprietarioId);
        if (lista.isEmpty()) {
            throw new RecursoNaoEncontradoException(
                "Nenhum espaco encontrado para o proprietario com ID: " + proprietarioId
            );
        }
        return lista;
    }

    /** Consultar espacos por tipo de evento (pesquisa parcial, case-insensitive) */
    @Transactional(readOnly = true)
    public List<Espaco> buscarPorTipoEvento(String tipoEvento) {
        List<Espaco> lista = espacoRepository.findByTipoEventoContainingIgnoreCase(tipoEvento);
        if (lista.isEmpty()) {
            throw new RecursoNaoEncontradoException(
                "Nenhum espaco encontrado para o tipo de evento: " + tipoEvento
            );
        }
        return lista;
    }
}
