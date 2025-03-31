package br.com.projeto.contabilidade.service;

import br.com.projeto.contabilidade.model.Arquivo;
import br.com.projeto.contabilidade.model.Cliente;
import br.com.projeto.contabilidade.model.User;
import br.com.projeto.contabilidade.repository.ArquivoRepository;
import br.com.projeto.contabilidade.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ArquivoRepository arquivoRepository;

    @InjectMocks
    private ClienteService clienteService;

    private User user;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("Usuario Test");

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setUser(user);
        cliente.setNome("Cliente Test");
    }

    @Test
    @DisplayName("Should return all the Clients")
    void listarClientes() {
        // arrange
        when(clienteRepository.findAllByUserUsername(user.getUsername())).thenReturn(Collections.singletonList(cliente));

        //act
        List<Cliente> resultado = clienteService.listarClientes(user.getUsername());

        //assert
        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNome()).isEqualTo("Cliente Test");
        verify(clienteRepository, times(1)).findAllByUserUsername(user.getUsername());
    }

    @Test
    @DisplayName("Should return all the clients and the pagination")
    void listarClientesPaginacao() {
        // arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Cliente> paginaCliente = new PageImpl<>(Collections.singletonList(cliente));
        when(clienteRepository.findAllByUserUsername(user.getUsername(), pageable))
                .thenReturn(paginaCliente);

        // act
        Page<Cliente> resultado = clienteService.listarClientesPaginacao(user.getUsername(), pageable);

        // assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getNome()).isEqualTo("Cliente Test");
        verify(clienteRepository, times(1)).findAllByUserUsername(user.getUsername(), pageable);
    }

    @Test
    @DisplayName("Should return only one client based on the id")
    void listarCliente_seExistir() {
        // arrange
        when(clienteRepository.findById(cliente.getId())).thenReturn(Optional.of(cliente));

        // act
        Cliente resultado = clienteService.listarCliente(cliente.getId());

        // assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Cliente Test");
        verify(clienteRepository, times(1)).findById(cliente.getId());
    }

    @Test
    @DisplayName("Should throw an exception if the id doesn't exist")
    void listarCliente_seNaoExistir() {
        // arrange
        when(clienteRepository.findById(999L)).thenReturn(Optional.empty());

        // act assert
        assertThatThrownBy(() -> clienteService.listarCliente(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Cliente não encontrado");
    }

    @Test
    @DisplayName("Should save a cliente successfully")
    void salvarCliente_comSucesso() {
        // arrange
        when(clienteRepository.save(cliente)).thenReturn(cliente);

        // act
        Cliente resultado = clienteService.salvarCliente(cliente);
        // assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Cliente Test");
        verify(clienteRepository, times(1)).save(cliente);
    }

    @Test
    @DisplayName("Should throw an exception if cliente is null")
    void salvarCliente_clienteNulo() {
        // arrange
        when(clienteRepository.save(null)).thenThrow(IllegalArgumentException.class);

        // act assert
        assertThatThrownBy(() -> clienteService.salvarCliente(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should delete a client based on id")
    void deletarCliente() throws IOException {
        // arrange
        Arquivo arquivo1 = new Arquivo();
        arquivo1.setCliente(cliente);
        arquivo1.setNomeArquivo("documento1.pdf");
        arquivo1.setCaminhoArquivo("\\uploads\\documento1.pdf");

        Arquivo arquivo2 = new Arquivo();
        arquivo2.setCliente(cliente);
        arquivo2.setNomeArquivo("documento2.pdf");
        arquivo2.setCaminhoArquivo("\\uploads\\documento2.pdf");

        when(arquivoRepository.findAllByClienteId(cliente.getId())).thenReturn(Arrays.asList(arquivo1, arquivo2));
        doNothing().when(clienteRepository).deleteById(cliente.getId());
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.deleteIfExists(any(Path.class)))
                    .thenReturn(true);

            // act
            clienteService.deletarCliente(cliente.getId());

            // assert
            verify(arquivoRepository, times(1)).findAllByClienteId(cliente.getId());
            verify(clienteRepository, times(1)).deleteById(cliente.getId());
            mockedFiles.verify(() -> Files.deleteIfExists(Paths.get(arquivo1.getCaminhoArquivo())));
            mockedFiles.verify(() -> Files.deleteIfExists(Paths.get(arquivo2.getCaminhoArquivo())));
        }
    }
}