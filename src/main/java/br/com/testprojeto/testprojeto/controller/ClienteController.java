/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.testprojeto.testprojeto.controller;

import br.com.testprojeto.testprojeto.model.Cliente;
import br.com.testprojeto.testprojeto.service.ClienteService;
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

/**
 *
 * @author PEDRO
 */
@Controller
@RequestMapping("/api/cliente")
public class ClienteController {
    
    @Autowired
    private ClienteService clienteService;
    
    @GetMapping
    public String listarClientes(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Cliente> clientesPage = clienteService.listarClientesPaginacao(pageable);
        model.addAttribute("clientes", clientesPage.getContent());
        model.addAttribute("totalPages", clientesPage.getTotalPages());
        model.addAttribute("totalItems", clientesPage.getTotalElements());
        model.addAttribute("currentPage", page);
        model.addAttribute("size", size);
        return "cliente/listar-cliente";
    }
    
    @GetMapping("/formulario")
    public String formularioClientes(@ModelAttribute("cliente") Cliente cliente) {
        return "cliente/formulario-cliente";
    }
    
    @PostMapping("salvar")
    public String salvarClientes(@ModelAttribute("cliente") Cliente cliente) {
        if (cliente.getId() != null) {
            Cliente clienteExistente = clienteService.listarCliente(cliente.getId());
            clienteExistente.setNome(cliente.getNome());
            clienteService.salvarCliente(clienteExistente);
            System.out.println("11111111111111111111111");
        } else {
            System.out.println("222222222222222222222");
            clienteService.salvarCliente(cliente);
        }
        return "redirect:/api/cliente";
    }
    
    @GetMapping("/editar/{id}")
    public String editarClientes(@PathVariable("id") Long id,
            Model model) {
        Cliente cliente = clienteService.listarCliente(id);
        model.addAttribute("cliente", cliente);
        return "cliente/editar-cliente";
    }
    
    @GetMapping("/visualizar/{id}")
    public String visualizarCliente(@PathVariable("id") Long id,
            Model model) {
        Cliente cliente = clienteService.listarCliente(id);
        model.addAttribute("cliente", cliente);
        return "cliente/visualizar-cliente";
    }
    
    @PostMapping("/deletar/{id}")
    public String deletarCliente(@PathVariable("id") Long id) {
        clienteService.deletarCliente(id);
        return "redirect:/api/cliente";
    }
}
