/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.testprojeto.testprojeto.controller;

import br.com.testprojeto.testprojeto.config.annotation.EndpointProtegido;
import br.com.testprojeto.testprojeto.model.Cliente;
import br.com.testprojeto.testprojeto.service.ClienteService;
import br.com.testprojeto.testprojeto.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

import java.io.IOException;
import java.security.Principal;

/**
 *
 * @author PEDRO
 */
@Tag(name = "CRUD Clientes", description = "Gerenciamento de clientes no sistema")
@Controller
@RequestMapping("/api/cliente")
public class ClienteController {
    
    @Autowired
    private ClienteService clienteService;

    @Autowired
    private UserService userService;

    @Operation(summary = "Lista os clientes",
            description = "Exibe a página com todos os clientes do usuário na sessão.",
            security = @SecurityRequirement(name = "cookieAuth"),
            method = "GET")
    @EndpointProtegido
    @GetMapping
    public String listarClientes(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Cliente> clientesPage = clienteService.listarClientesPaginacao(principal.getName(), pageable);

        model.addAttribute("clientes", clientesPage.getContent());
        model.addAttribute("totalPages", clientesPage.getTotalPages());
        model.addAttribute("totalItems", clientesPage.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        return "cliente/listar-cliente";
    }

    @Operation(summary = "Formulário de novos clientes",
            description = "Contêm os campos necessários para criação do cliente",
            security = @SecurityRequirement(name = "cookieAuth"),
            method = "GET"
    )
    @EndpointProtegido
    @GetMapping("/formulario")
    public String formularioClientes(@ModelAttribute("cliente") Cliente cliente) {
        return "cliente/formulario-cliente";
    }

    @Operation(summary = "Salva os clientes",
            description = "Realizar o salvamento dos novos clientes ou dos editados no banco de dados",
            security = @SecurityRequirement(name = "cookieAuth"),
            method = "POST"
    )
    @EndpointProtegido
    @PostMapping("salvar")
    public String salvarClientes(@ModelAttribute("cliente") Cliente cliente,
                                 Principal principal) {
        if (cliente.getUser() == null) {
            cliente.setUser(userService.listarPorUsername(principal.getName()));
        }

        if (cliente.getId() != null) {
            Cliente clienteExistente = clienteService.listarCliente(cliente.getId());
            clienteExistente.setNome(cliente.getNome());
            clienteService.salvarCliente(clienteExistente);
        } else {
            clienteService.salvarCliente(cliente);
        }
        return "redirect:/api/cliente";
    }

    @Operation(summary = "Edita um cliente",
            description = "Página onde um cliente específico pode ser editado baseado no id",
            security = @SecurityRequirement(name = "cookieAuth"),
            method = "GET"
    )
    @EndpointProtegido
    @GetMapping("/editar/{id}")
    public String editarClientes(@PathVariable("id") Long id,
            Model model) {
        Cliente cliente = clienteService.listarCliente(id);
        model.addAttribute("cliente", cliente);
        return "cliente/editar-cliente";
    }

    @Operation(summary = "Visualiza um cliente",
            description = "Página onde um cliente específico é vizualizado baseado no id",
            security = @SecurityRequirement(name = "cookieAuth"),
            method = "GET"
    )
    @EndpointProtegido
    @GetMapping("/visualizar/{id}")
    public String visualizarCliente(@PathVariable("id") Long id,
            Model model) {
        Cliente cliente = clienteService.listarCliente(id);
        model.addAttribute("cliente", cliente);
        return "cliente/visualizar-cliente";
    }

    @Operation(summary = "Deleta um cliente",
            description = "Deleta um cliente específico baseado no id",
            security = @SecurityRequirement(name = "cookieAuth"),
            method = "POST"
    )
    @EndpointProtegido
    @PostMapping("/deletar/{id}")
    public String deletarCliente(@PathVariable("id") Long id) {
        try {
            clienteService.deletarCliente(id);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return "redirect:/api/cliente";
    }
}
