/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.testprojeto.testprojeto.service;

import br.com.testprojeto.testprojeto.model.Arquivo;
import br.com.testprojeto.testprojeto.model.Cliente;
import br.com.testprojeto.testprojeto.repository.ArquivoRepository;
import br.com.testprojeto.testprojeto.repository.ClienteRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 *
 * @author PEDRO
 */
@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private ArquivoRepository arquivoRepository;

    public List<Cliente> listarClientes(String nome) {
        return clienteRepository.findAllByUserUsername(nome);
    }

    public Page<Cliente> listarClientesPaginacao(String nome,
                                                 Pageable pageable) {
        return clienteRepository.findAllByUserUsername(nome , pageable);
    }

    public Cliente listarCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("cliente não encontrado"));
    }

    public void salvarCliente(Cliente cliente) {
        clienteRepository.save(cliente);
    }

    public void deletarCliente(Long id) throws IOException {
        List<Arquivo> arquivos = arquivoRepository.findAllByClienteId(id);

        if (!arquivos.isEmpty()) {
            for (Arquivo arquivo : arquivos) {
                Files.deleteIfExists(Paths.get(arquivo.getCaminhoArquivo()));
            }
        }
        clienteRepository.deleteById(id);
    }
}
