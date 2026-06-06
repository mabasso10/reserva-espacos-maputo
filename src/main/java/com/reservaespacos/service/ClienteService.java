package com.reservaespacos.service;

import com.reservaespacos.exception.RecursoNaoEncontradoException;
import com.reservaespacos.exception.RegraDeNegocioException;
import com.reservaespacos.model.Cliente;
import com.reservaespacos.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Cliente registar(Cliente cliente) {
        if (clienteRepository.existsByNumeroDocumento(cliente.getNumeroDocumento())) {
            throw new RegraDeNegocioException(
                "Ja existe um cliente com o numero de documento: " + cliente.getNumeroDocumento()
            );
        }
        return clienteRepository.save(cliente);
    }

    public Cliente actualizar(Long id, Cliente dadosNovos) {
        Cliente cliente = buscarPorId(id);
        cliente.setNome(dadosNovos.getNome());
        cliente.setApelido(dadosNovos.getApelido());
        cliente.setTelefone(dadosNovos.getTelefone());
        cliente.setBairro(dadosNovos.getBairro());
        cliente.setTipoDocumento(dadosNovos.getTipoDocumento());
        if (!cliente.getNumeroDocumento().equals(dadosNovos.getNumeroDocumento())) {
            if (clienteRepository.existsByNumeroDocumento(dadosNovos.getNumeroDocumento())) {
                throw new RegraDeNegocioException(
                    "Numero de documento ja em uso: " + dadosNovos.getNumeroDocumento()
                );
            }
            cliente.setNumeroDocumento(dadosNovos.getNumeroDocumento());
        }
        return clienteRepository.save(cliente);
    }

    public void remover(Long id) {
        Cliente cliente = buscarPorId(id);
        clienteRepository.delete(cliente);
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente nao encontrado com ID: " + id));
    }

    @Transactional(readOnly = true)
    public List<Cliente> buscarPorBairro(String bairro) {
        List<Cliente> lista = clienteRepository.findByBairroIgnoreCase(bairro);
        if (lista.isEmpty()) {
            throw new RecursoNaoEncontradoException("Nenhum cliente encontrado no bairro: " + bairro);
        }
        return lista;
    }
}
