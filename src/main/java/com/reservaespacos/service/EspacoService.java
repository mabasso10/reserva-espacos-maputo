package com.reservaespacos.service;

import com.reservaespacos.dto.Dtos;
import com.reservaespacos.exception.RecursoNaoEncontradoException;
import com.reservaespacos.exception.RegraDeNegocioException;
import com.reservaespacos.model.Espaco;
import com.reservaespacos.model.Proprietario;
import com.reservaespacos.repository.EspacoRepository;
import com.reservaespacos.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class EspacoService {

    @Autowired private EspacoRepository espacoRepository;
    @Autowired private ProprietarioService proprietarioService;
    @Autowired private ReservaRepository reservaRepository;

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
        espaco.setDisponibilidade(dto.getDisponibilidade() != null ? dto.getDisponibilidade() : true);
        return espacoRepository.save(espaco);
    }

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
        espaco.setDisponibilidade(dto.getDisponibilidade() != null ? dto.getDisponibilidade() : espaco.getDisponibilidade());
        return espacoRepository.save(espaco);
    }

    /**
     * CORRIGIDO: acumula a nova foto na lista em vez de sobrescrever.
     * Máximo de 7 fotos por espaço.
     */
    public Espaco adicionarFoto(Long id, String nomeFicheiro) {
        Espaco espaco = buscarPorId(id);
        List<String> fotos = espaco.getFotos();
        if (fotos == null) fotos = new ArrayList<>();
        if (fotos.size() >= 7) {
            throw new RegraDeNegocioException(
                "Limite máximo de 7 fotos atingido. Remova uma foto antes de adicionar outra."
            );
        }
        fotos.add(nomeFicheiro);
        espaco.setFotos(fotos);
        return espacoRepository.save(espaco);
    }

    /**
     * Remove uma foto específica da lista pelo nome do ficheiro.
     */
    public Espaco removerFoto(Long id, String nomeFicheiro) {
        Espaco espaco = buscarPorId(id);
        List<String> fotos = new ArrayList<>(espaco.getFotos());
        boolean removido = fotos.remove(nomeFicheiro);
        if (!removido) {
            throw new RecursoNaoEncontradoException("Foto não encontrada: " + nomeFicheiro);
        }
        espaco.setFotos(fotos);
        return espacoRepository.save(espaco);
    }

    /**
     * CORRIGIDO: verifica reservas activas antes de apagar.
     * Lança RegraDeNegocioException com mensagem clara em vez de DataIntegrityViolationException.
     */
    public void remover(Long id) {
        Espaco espaco = buscarPorId(id);
        long reservasActivas = reservaRepository.findByEspacoId(id).stream()
            .filter(r -> {
                com.reservaespacos.model.Reserva.Estado e = r.getEstado();
                return e == com.reservaespacos.model.Reserva.Estado.PENDENTE
                    || e == com.reservaespacos.model.Reserva.Estado.CONFIRMADA;
            }).count();
        if (reservasActivas > 0) {
            throw new RegraDeNegocioException(
                "Não é possível apagar o espaço '" + espaco.getNomeEspaco() +
                "' porque tem " + reservasActivas + " reserva(s) activa(s) (PENDENTE ou CONFIRMADA). " +
                "Cancele ou conclua as reservas primeiro."
            );
        }
        espacoRepository.delete(espaco);
    }

    @Transactional(readOnly = true)
    public List<Espaco> listarTodos() {
        return espacoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Espaco buscarPorId(Long id) {
        return espacoRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Espaco nao encontrado com ID: " + id));
    }

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
