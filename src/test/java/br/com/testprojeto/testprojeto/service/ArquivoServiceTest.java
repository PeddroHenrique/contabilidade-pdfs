package br.com.testprojeto.testprojeto.service;

import br.com.testprojeto.testprojeto.model.Arquivo;
import br.com.testprojeto.testprojeto.model.Cliente;
import br.com.testprojeto.testprojeto.model.User;
import br.com.testprojeto.testprojeto.repository.ArquivoRepository;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArquivoServiceTest {

    @Mock
    private ArquivoRepository arquivoRepository;

    @InjectMocks
    private ArquivoService arquivoService;

    private User user;
    private Cliente cliente;
    private Arquivo arquivo;
    private MultipartFile mockMultipartFile;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("Usuario Test");

        cliente = new Cliente();
        cliente.setNome("Cliente Test");
        cliente.setUser(user);

        // É necessário o caminho do arquivo para que os testes de salvamento funcionem
        arquivo = new Arquivo();
        arquivo.setId(1L);
        arquivo.setNomeArquivo("documento.pdf");
        arquivo.setCaminhoArquivo("\\uploads\\documento.pdf");
        arquivo.setCliente(cliente);

        // Cria o mock de um MultipartFile
        mockMultipartFile = new MockMultipartFile(
                "file", "documento.pdf", "application/pdf", "conteudo do arquivo".getBytes()
        );

        // Cria o diretório /uploads
        ReflectionTestUtils.setField(arquivoService, "uploadDir", "/uploads");
    }

    @Test
    @DisplayName("Should return all documents")
    void listarArquivos() {
        // Arrange
        List<Arquivo> arquivos = Arrays.asList(arquivo);
        when(arquivoRepository.findAll()).thenReturn(arquivos);

        // Act
        List<Arquivo> resultado = arquivoService.listarArquivos();

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getNomeArquivo()).isEqualTo("documento.pdf");
        verify(arquivoRepository).findAll();
    }

    @Test
    @DisplayName("Should return a document when id exists")
    void listarArquivoPorId_seExistir() {
        when(arquivoRepository.findById(1L)).thenReturn(Optional.of(arquivo));

        Arquivo resultado = arquivoService.listarArquivoPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNomeArquivo()).isEqualTo("documento.pdf");
        verify(arquivoRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return a document when id exists")
    void listarArquivoPorId_seNaoExistir() {
        when(arquivoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> arquivoService.listarArquivoPorId(1L))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Arquivo não encontrado");
    }

    @Test
    void listarArquivosPaginacao() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Arquivo> paginaArquivo = new PageImpl<>(Arrays.asList(arquivo));
        when(arquivoRepository.findArquivosByClienteUsuario("Usuario Test", pageable))
                .thenReturn(paginaArquivo);

        Page<Arquivo> resultado = arquivoService.listarArquivosPaginacao("Usuario Test", pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getNomeArquivo()).isEqualTo("documento.pdf");
        verify(arquivoRepository).findArquivosByClienteUsuario("Usuario Test", pageable);
    }

    @Test
    void listarArquivosPorNomeCliente() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Arquivo> paginaArquivo = new PageImpl<>(Arrays.asList(arquivo));
        when(arquivoRepository.findArquivosByClienteNomeAndUsuario("Cliente Test", "Usuario Test", pageable))
                .thenReturn(paginaArquivo);

        Page<Arquivo> resultado = arquivoService.listarArquivosPorNomeCliente("Cliente Test", "Usuario Test", pageable);

        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).getNomeArquivo()).isEqualTo("documento.pdf");
        verify(arquivoRepository).findArquivosByClienteNomeAndUsuario("Cliente Test", "Usuario Test", pageable);
    }

    @Test
    @DisplayName("Should save a file successfully")
    void salvarArquivo_comSucesso() throws IOException {
        arquivo.setId(null);

        when(arquivoRepository.save(arquivo)).thenReturn(arquivo);

        arquivoService.salvarArquivo(mockMultipartFile, arquivo);

        verify(arquivoRepository).save(arquivo);
        assertThat(arquivo.getNomeArquivo()).isEqualTo("documento.pdf");
        assertThat(arquivo.getCaminhoArquivo()).isNotNull();
    }

    @Test
    @DisplayName("Should update an existing file")
    void salvarArquivo_atualizarArquivoExistente() throws IOException{
        // arrange
        when(arquivoRepository.findById(arquivo.getId())).thenReturn(Optional.of(arquivo));
        when(arquivoRepository.save(arquivo)).thenReturn(arquivo);

        // act
        arquivoService.salvarArquivo(mockMultipartFile, arquivo);

        // assert
        verify(arquivoRepository).save(arquivo);
        verify(arquivoRepository).findById(arquivo.getId());
        assertThat(arquivo.getNomeArquivo()).isEqualTo("documento.pdf");
        assertThat(arquivo.getCaminhoArquivo()).isNotNull();
        assertThat(arquivo.getCaminhoArquivo()).isNotEqualTo("\\uploads\\documento.pdf");
    }

    @Test
    @DisplayName("Should throw exception when file is empty")
    void salvarArquivo_arquivoVazio() {
        // arrange
        mockMultipartFile = new MockMultipartFile(
                "file", "", "application/pdf", new byte[0]
        );
        arquivo.setId(null);

        // act and assert
        assertThatThrownBy(() ->
                arquivoService.salvarArquivo(mockMultipartFile, arquivo)
        ).isInstanceOf(RuntimeException.class)
        .hasMessage("Arquivo não pode ser nulo ou vazio");
    }

    @Test
    @DisplayName("Should throw exception when updating non-existing file")
    void salvarArquivo_arquivoNaoEncontrado() {
        arquivo.setId(999L);

        assertThatThrownBy(() ->
                arquivoService.salvarArquivo(mockMultipartFile, arquivo)
        ).isInstanceOf(RuntimeException.class)
        .hasMessage("Arquivo não encontrado");
    }

    @Test
    @DisplayName("Should delete the file successfully")
    void deletarArquivo_comSucesso() throws IOException {
        // arrange
        when(arquivoRepository.findById(arquivo.getId())).thenReturn(Optional.of(arquivo));
        doNothing().when(arquivoRepository).delete(arquivo);
        try(MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.deleteIfExists(Paths.get(arquivo.getCaminhoArquivo())))
                    .thenReturn(true);

            // act
            arquivoService.deletarArquivo(arquivo.getId());

            // assert
            verify(arquivoRepository).findById(arquivo.getId());
            verify(arquivoRepository).delete(arquivo);
            mockedFiles.verify(() -> Files.deleteIfExists(Paths.get(arquivo.getCaminhoArquivo())));
        }
    }

    @Test
    @DisplayName("Should delete the file successfully")
    void deletarArquivo_arquivoNaoEncontrado() {

        when(arquivoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> arquivoService.deletarArquivo(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Arquivo não encontrado");

        verify(arquivoRepository).findById(999L);
        verify(arquivoRepository, never()).delete(any());
    }
}