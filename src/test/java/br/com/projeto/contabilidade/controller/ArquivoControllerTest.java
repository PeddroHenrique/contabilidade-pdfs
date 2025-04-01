package br.com.projeto.contabilidade.controller;

import br.com.projeto.contabilidade.model.Arquivo;
import br.com.projeto.contabilidade.model.Cliente;
import br.com.projeto.contabilidade.model.User;
import br.com.projeto.contabilidade.service.ArquivoService;
import br.com.projeto.contabilidade.service.ClienteService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArquivoControllerTest {

    @Mock
    private ArquivoService arquivoService;

    @Mock
    private ClienteService clienteService;

    @Mock
    private Principal principal;

    @Mock
    private Model model;

    @Mock
    private MultipartFile file;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ServletOutputStream servletOutputStream;

    @InjectMocks
    private ArquivoController arquivoController;

    private User user;
    private Cliente cliente;
    private List<Cliente> clientes;
    private Arquivo arquivo;
    private List<Arquivo> arquivos;
    private Page<Arquivo> arquivosPage;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("Usuario");

        cliente = new Cliente();
        cliente.setId(1L);
        cliente.setNome("Cliente");
        clientes = Arrays.asList(cliente);

        arquivo = new Arquivo();
        arquivo.setId(1L);
        arquivo.setNomeArquivo("Arquivo.pdf");
        arquivo.setCaminhoArquivo("/caminho/para/Arquivo.pdf");
        arquivo.setCliente(cliente);

        arquivos = Arrays.asList(arquivo);
        arquivosPage = new PageImpl<>(arquivos);
    }

    @Test
    @DisplayName("Should return the correct view for listarArquivos with all arquivos")
    void listarArquivos_semSearch() {
        Pageable pageable = PageRequest.of(0, 10);
        when(arquivoService.listarArquivosPaginacao(anyString(), any(Pageable.class))).thenReturn(arquivosPage);
        when(principal.getName()).thenReturn("Usuario");

        String viewName = arquivoController.listarArquivos(0, 10, null, principal, model);

        assertThat(viewName).isEqualTo("arquivo/listar-arquivo");
        verify(arquivoService).listarArquivosPaginacao("Usuario", pageable);
        verify(principal).getName();
        verify(model).addAttribute("arquivos", arquivos);
        verify(model).addAttribute("totalPages", arquivosPage.getTotalPages());
        verify(model).addAttribute("totalItems", arquivosPage.getTotalElements());
        verify(model).addAttribute("currentPage", 0);
        verify(model).addAttribute("size", 10);
        verify(model).addAttribute("search", null);
    }

    @Test
    @DisplayName("Should return the correct view for listarArquivos with search")
    void listarArquivos_comSearch() {
        String search = "teste";
        Pageable pageable = PageRequest.of(0, 10);
        when(arquivoService.listarArquivosPorNomeCliente(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(arquivosPage);
        when(principal.getName()).thenReturn("Usuario");

        String viewName = arquivoController.listarArquivos(0, 10, search, principal, model);

        assertThat(viewName).isEqualTo("arquivo/listar-arquivo");
        verify(arquivoService).listarArquivosPorNomeCliente(search, "Usuario", pageable);
        verify(principal).getName();
        verify(model).addAttribute("arquivos", arquivos);
        verify(model).addAttribute("totalPages", arquivosPage.getTotalPages());
        verify(model).addAttribute("totalItems", arquivosPage.getTotalElements());
        verify(model).addAttribute("currentPage", 0);
        verify(model).addAttribute("size", 10);
        verify(model).addAttribute("search", search);
    }

    @Test
    @DisplayName("Should return the correct page for formularioArquivos")
    void formularioArquivos() {
        when(clienteService.listarClientes(anyString())).thenReturn(clientes);
        when(principal.getName()).thenReturn("Usuario");

        String viewName = arquivoController.formularioArquivos(new Arquivo(), model, principal);

        assertThat(viewName).isEqualTo("arquivo/formulario-arquivo");
        verify(clienteService).listarClientes("Usuario");
        verify(principal).getName();
        verify(model).addAttribute("clientes", clientes);
    }

    @Test
    @DisplayName("Should save without throw any exception")
    void salvarArquivos_semExcecao() throws IOException {
        doNothing().when(arquivoService).salvarArquivo(any(MultipartFile.class), any(Arquivo.class));

        String viewName = arquivoController.salvarArquivos(arquivo, file, model);

        assertThat(viewName).isEqualTo("redirect:/api/arquivo");
        verify(arquivoService).salvarArquivo(file, arquivo);
    }

    @Test
    @DisplayName("Should throw an exception")
    void salvarArquivos_comExcecao() throws IOException {
        doThrow(new IOException()).when(arquivoService)
                .salvarArquivo(any(MultipartFile.class), any(Arquivo.class));

        String viewName = arquivoController.salvarArquivos(arquivo, file, model);

        assertThat(viewName).isEqualTo("redirect:/api/arquivo");
        verify(arquivoService, times(1)).salvarArquivo(file, arquivo);
        assertThatThrownBy(() -> arquivoService.salvarArquivo(file, arquivo))
                .isInstanceOf(IOException.class);

    }

    @Test
    @DisplayName("Should return the correct view for editarArquivos")
    void editarArquivos() {
        when(arquivoService.listarArquivoPorId(anyLong())).thenReturn(arquivo);
        when(clienteService.listarClientes(anyString())).thenReturn(clientes);
        when(principal.getName()).thenReturn("Usuario");

        String viewName = arquivoController.editarArquivos(1L, model, principal);

        assertThat(viewName).isEqualTo("arquivo/editar-arquivo");
        verify(arquivoService).listarArquivoPorId(1L);
        verify(clienteService).listarClientes("Usuario");
        verify(principal).getName();
        verify(model).addAttribute("clientes", clientes);
        verify(model).addAttribute("arquivo", arquivo);
    }

    @Test
    @DisplayName("Should return the correct page for visualizarArquivos")
    void visualizarArquivos() {
        when(arquivoService.listarArquivoPorId(anyLong())).thenReturn(arquivo);

        String viewName = arquivoController.visualizarArquivos(1L, model);

        assertThat(viewName).isEqualTo("arquivo/visualizar-arquivo");
        verify(arquivoService).listarArquivoPorId(1L);
        verify(model).addAttribute("arquivo", arquivo);
    }

    @Test
    @DisplayName("Should delete without any exception")
    void deletarArquivos_semExcecao() throws IOException {
        doNothing().when(arquivoService).deletarArquivo(anyLong());

        String viewName = arquivoController.deletarArquivos(1L, model);

        assertThat(viewName).isEqualTo("redirect:/api/arquivo");
        verify(arquivoService).deletarArquivo(1L);
    }

    @Test
    @DisplayName("Should return an exception")
    void deletarArquivos_comExcecao() throws IOException {
        doThrow(IOException.class).when(arquivoService)
                .deletarArquivo(anyLong());

        String viewName = arquivoController.deletarArquivos(1L, model);

        assertThat(viewName).isEqualTo("redirect:/api/arquivo");
        assertThatThrownBy(() -> arquivoService.deletarArquivo(1L))
                .isInstanceOf(IOException.class);
    }

    @Test
    @DisplayName("Should return a new page with an existing file")
    void visualizarPdf_arquivoExiste() throws IOException {
        when(arquivoService.listarArquivoPorId(anyLong())).thenReturn(arquivo);

        try (MockedStatic<Paths> pathMockedStatic = mockStatic(Paths.class);
             MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {

            Path mockPath = mock(Path.class);
            when(response.getOutputStream()).thenReturn(servletOutputStream);
            pathMockedStatic.when(() -> Paths.get(anyString())).thenReturn(mockPath);
            filesMockedStatic.when(() -> Files.exists(any(Path.class))).thenReturn(true);
            doNothing().when(response).setContentType(anyString());
            doNothing().when(response).setHeader(anyString(), anyString());

            arquivoController.visualizarPdf(1L, response);

            verify(arquivoService).listarArquivoPorId(1L);
            verify(response).setContentType("application/pdf");
            verify(response).setHeader(eq("Content-Disposition"), anyString());
            filesMockedStatic.verify(() -> Files.exists(mockPath));
            filesMockedStatic.verify(() -> Files.copy(eq(mockPath), any()));
            verify(servletOutputStream).flush();
        }
    }

    @Test
    @DisplayName("Should return a new page with an existing file")
    void visualizarPdf_arquivoNaoExiste() throws IOException {
        when(arquivoService.listarArquivoPorId(anyLong())).thenReturn(arquivo);

        try (MockedStatic<Paths> pathMockedStatic = mockStatic(Paths.class);
             MockedStatic<Files> filesMockedStatic = mockStatic(Files.class)) {

            Path mockPath = mock(Path.class);
            pathMockedStatic.when(() -> Paths.get(anyString())).thenReturn(mockPath);
            filesMockedStatic.when(() -> Files.exists(any(Path.class))).thenReturn(false);

            arquivoController.visualizarPdf(1L, response);

            verify(arquivoService).listarArquivoPorId(1L);
            filesMockedStatic.verify(() -> Files.exists(mockPath));
            verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Arquivo não encontrado");
        }
    }
}