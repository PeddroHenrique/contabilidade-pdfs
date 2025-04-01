package br.com.projeto.contabilidade.controller;

import br.com.projeto.contabilidade.model.Cliente;
import br.com.projeto.contabilidade.model.User;
import br.com.projeto.contabilidade.service.ClienteService;
import br.com.projeto.contabilidade.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteControllerTest {

    @Mock
    private ClienteService clienteService;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private Principal principal;

    @InjectMocks
    private ClienteController clienteController;

    private Cliente cliente;
    private User user;
    private List<Cliente> clientes;
    private Page<Cliente> clientesPage;

    @BeforeEach
    void setUp(){
        user = new User();
        user.setId(1L);
        user.setUsername("Usuario");

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Cliente");
        cliente.setUser(user);

        clientes = Arrays.asList(cliente);
        clientesPage = new PageImpl<>(clientes);
    }

    @Test
    @DisplayName("Should return the correct view for listarClientes")
    void listarClientes() {
        Pageable pageable = PageRequest.of(0, 10);
        when(clienteService.listarClientesPaginacao(anyString(), any(Pageable.class))).thenReturn(clientesPage);
        when(principal.getName()).thenReturn("Usuario");

        String viewName = clienteController.listarClientes(0, 10, principal, model);

        assertThat(viewName).isEqualTo("cliente/listar-cliente");
        verify(clienteService).listarClientesPaginacao("Usuario", pageable);
        verify(model).addAttribute("clientes", clientes);
        verify(model).addAttribute("totalPages", clientesPage.getTotalPages());
        verify(model).addAttribute("totalItems", clientesPage.getTotalElements());
        verify(model).addAttribute("currentPage", 0);
        verify(model).addAttribute("size", 10);
    }

    @Test
    @DisplayName("Should return the correct view for formularioClientes")
    void formularioClientes() {
        String viewName = clienteController.formularioClientes(new Cliente());

        assertThat(viewName).isEqualTo("cliente/formulario-cliente");
    }

    @Test
    @DisplayName("Should save a new user and redirect")
    void salvarClientes_comClienteNovo() {
        Cliente novoCliente = new Cliente();
        novoCliente.setNome("Novo Cliente");

        when(userService.listarPorUsername(anyString())).thenReturn(user);
        when(principal.getName()).thenReturn("Usuario");
        when(clienteService.salvarCliente(any(Cliente.class))).thenReturn(novoCliente);

        String viewName = clienteController.salvarClientes(novoCliente, principal);

        assertThat(viewName).isEqualTo("redirect:/api/cliente");
        verify(userService).listarPorUsername("Usuario");
        verify(principal).getName();
        verify(clienteService).salvarCliente(novoCliente);
    }

    @Test
    @DisplayName("Should save a user that already exist and redirect")
    void salvarClientes_comClienteExistente() {
        when(clienteService.listarCliente(anyLong())).thenReturn(cliente);
        when(clienteService.salvarCliente(any(Cliente.class))).thenReturn(cliente);

        String viewName = clienteController.salvarClientes(cliente, principal);

        assertThat(viewName).isEqualTo("redirect:/api/cliente");
        verify(clienteService).listarCliente(1L);
        verify(clienteService).salvarCliente(cliente);
    }

    @Test
    @DisplayName("Should return the correct view for editarClientes")
    void editarCliente() {
        when(clienteService.listarCliente(anyLong())).thenReturn(cliente);

        String viewName = clienteController.editarCliente(1L, model);

        assertThat(viewName).isEqualTo("cliente/editar-cliente");
        verify(clienteService).listarCliente(1L);
        verify(model).addAttribute("cliente", cliente);
    }

    @Test
    @DisplayName("Should return the correct view for visualizarCliente")
    void visualizarCliente() {
        when(clienteService.listarCliente(anyLong())).thenReturn(cliente);

        String viewName = clienteController.visualizarCliente(1L, model);

        assertThat(viewName).isEqualTo("cliente/visualizar-cliente");
        verify(model).addAttribute("cliente", cliente);
    }

    @Test
    void deletarCliente() throws IOException {
        doNothing().when(clienteService).deletarCliente(anyLong());

        String viewName = clienteController.deletarCliente(1L);

        assertThat(viewName).isEqualTo("redirect:/api/cliente");
        verify(clienteService).deletarCliente(1L);
    }
}