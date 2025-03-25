/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.testprojeto.testprojeto.controller;

import br.com.testprojeto.testprojeto.model.Arquivo;
import br.com.testprojeto.testprojeto.service.ArquivoService;
import br.com.testprojeto.testprojeto.service.ClienteService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

/**
 *
 * @author PEDRO
 */
@Controller
@RequestMapping("/api/arquivo")
public class ArquivoController {

    @Autowired
    private ArquivoService arquivoService;

    @Autowired
    private ClienteService clienteService;

    @GetMapping()
    public String listarArquivos(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            Principal principal,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Arquivo> arquivosPage;

        if (!StringUtils.isEmptyOrWhitespace(search)) {
            arquivosPage = arquivoService.listarArquivosPorNomeCliente(search, principal.getName(), pageable);
        } else {
            arquivosPage = arquivoService.listarArquivosPaginacao(principal.getName(), pageable);
        }

        model.addAttribute("arquivos", arquivosPage.getContent());
        model.addAttribute("totalPages", arquivosPage.getTotalPages());
        model.addAttribute("totalItems", arquivosPage.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        model.addAttribute("search", search);
        return "arquivo/listar-arquivo";
    }

    @GetMapping("/formulario")
    public String formularioArquivos(Arquivo arquivo, Model model, Principal principal) {
        model.addAttribute("clientes", clienteService.listarClientes(principal.getName()));
        return "arquivo/formulario-arquivo";
    }

    @PostMapping("/salvar")
    public String salvarArquivos(@ModelAttribute("arquivo") Arquivo arquivo,
            @RequestParam("file") MultipartFile file,
            Model model) {
        try {
            arquivoService.salvarArquivo(file, arquivo);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return "redirect:/api/arquivo";
    }

    @GetMapping("/editar/{id}")
    public String editarArquivos(@PathVariable("id") Long id,
                                 Model model,
                                 Principal principal) {
        Arquivo arquivo = arquivoService.listarArquivoPorId(id);
        model.addAttribute("clientes", clienteService.listarClientes(principal.getName()));
        model.addAttribute("arquivo", arquivo);
        return "arquivo/editar-arquivo";
    }

    @GetMapping("/visualizar/{id}")
    public String visualizarArquivos(@PathVariable("id") Long id,
            Model model) {
        Arquivo arquivo = arquivoService.listarArquivoPorId(id);
        model.addAttribute("arquivo", arquivo);
        return "arquivo/visualizar-arquivo";
    }

    @PostMapping("/deletar/{id}")
    public String deletarArquivos(@PathVariable("id") Long id,
            Model model) {
        try {
            arquivoService.deletarArquivo(id);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return "redirect:/api/arquivo";
    }
    
    @GetMapping("vizualizar-pdf/{id}")
    public void visualizarPdf(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
        Arquivo arquivo = arquivoService.listarArquivoPorId(id);
        Path filePath = Paths.get(arquivo.getCaminhoArquivo());
        
        if (Files.exists(filePath)) {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=\"" + arquivo.getNomeArquivo());
            Files.copy(filePath, response.getOutputStream());
            response.getOutputStream().flush();
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Arquivo não encontrado");
        }
    }
}
