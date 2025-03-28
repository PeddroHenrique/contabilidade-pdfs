/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.testprojeto.testprojeto.service;

import br.com.testprojeto.testprojeto.model.Arquivo;
import br.com.testprojeto.testprojeto.repository.ArquivoRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author PEDRO
 */
@Service
public class ArquivoService {

    @Value("${app.upload-dir}")
    private String uploadDir;

    @Autowired
    private ArquivoRepository arquivoRepository;

    public List<Arquivo> listarArquivos() {
        return arquivoRepository.findAll();
    }

    public Arquivo listarArquivoPorId(Long id) {
        return arquivoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Arquivo não encontrado"));
    }
    
    public Page<Arquivo> listarArquivosPaginacao(String username,
                                                 Pageable pageable) {
        return arquivoRepository.findArquivosByClienteUsuario(username, pageable);
    }

    public Page<Arquivo> listarArquivosPorNomeCliente(String nome, String usuario, Pageable pageable) {
        return arquivoRepository.findArquivosByClienteNomeAndUsuario(nome, usuario, pageable);
    }

    public void salvarArquivo(MultipartFile file, Arquivo arquivo) throws IOException {
        // verifica se o diretório existe, se não, cria
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        if (file == null || arquivo.getId() == null && file.isEmpty()) {
            throw new RuntimeException("Arquivo não pode ser nulo ou vazio");
        }

        if (arquivo.getId() != null) {
            if (!file.isEmpty()) {
                Arquivo arquivoExistente = arquivoRepository.findById(arquivo.getId())
                        .orElseThrow(() -> new RuntimeException("Arquivo não encontrado"));
                Files.deleteIfExists(Paths.get(arquivoExistente.getCaminhoArquivo()));
            }
        }

        // gera um nome único
        String nomeArquivo = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(nomeArquivo);

        // salva o arquivo no diretório
        Files.copy(file.getInputStream(), filePath);
        arquivo.setCaminhoArquivo(filePath.toString());
        arquivo.setNomeArquivo(file.getOriginalFilename());


        arquivoRepository.save(arquivo);
    }

    public void deletarArquivo(Long id) throws IOException {
        Arquivo arquivoExistente = arquivoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Arquivo não encontrado"));

        Files.deleteIfExists(Paths.get(arquivoExistente.getCaminhoArquivo()));

        arquivoRepository.delete(arquivoExistente);
    }
}
