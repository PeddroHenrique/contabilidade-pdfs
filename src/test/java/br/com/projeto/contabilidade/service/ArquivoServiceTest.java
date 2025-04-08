package br.com.projeto.contabilidade.service;

import br.com.projeto.contabilidade.model.Arquivo;
import br.com.projeto.contabilidade.model.Cliente;
import br.com.projeto.contabilidade.model.User;
import br.com.projeto.contabilidade.repository.ArquivoRepository;
import org.junit.jupiter.api.AfterEach;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
    private Path tempUploadDir;

    @BeforeEach
    void setUp() throws IOException {
        user = new User();
        user.setUsername("Usuario Test");

        cliente = new Cliente();
        cliente.setNome("Cliente Test");
        cliente.setUser(user);

        // Cria um diretório temporário exclusivo para cada teste
        tempUploadDir = Files.createTempDirectory("test-uploads-");

        // define o diretorio no arquivo service
        ReflectionTestUtils.setField(arquivoService, "uploadDir", tempUploadDir.toString());

        // É necessário o caminho do arquivo para que os testes de salvamento funcionem
        arquivo = new Arquivo();
        arquivo.setId(1L);
        arquivo.setNomeArquivo("documento.pdf");
        arquivo.setCaminhoArquivo(tempUploadDir.resolve("documento.pdf").toString());
        arquivo.setCliente(cliente);

        // Cria o mock de um MultipartFile
        mockMultipartFile = new MockMultipartFile(
                "file", "documento.pdf", "application/pdf", "conteudo do arquivo".getBytes()
        );
    }

    @AfterEach
    void tearDown() throws IOException {
        try {
            Files.walk(tempUploadDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            System.err.println("Falha ao deletar arquivo temporario" + path);
                        }
                    });
        } catch (IOException e) {
            System.err.println("Warning: Falha durante limpeza do diretório temporário");
        }
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

        // simula a geração de ID
        when(arquivoRepository.save(arquivo)).thenAnswer(invocation -> {
            Arquivo saved = invocation.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        arquivoService.salvarArquivo(mockMultipartFile, arquivo);

        verify(arquivoRepository).save(arquivo);
        assertThat(arquivo.getNomeArquivo()).isEqualTo("documento.pdf");
        assertThat(arquivo.getCaminhoArquivo()).isNotNull();
    }

    @Test
    @DisplayName("Should update an existing file")
    void salvarArquivo_atualizarArquivoExistente() throws IOException{
        Path existingFilePath = tempUploadDir.resolve("documento_antigo.pdf");
        Files.createDirectories(existingFilePath.getParent());
        Files.write(existingFilePath, "Conteudo antigo".getBytes());

        Arquivo arquivoExistente = new Arquivo();
        arquivoExistente.setId(1L);
        arquivoExistente.setCaminhoArquivo(existingFilePath.toString());

        // arrange
        when(arquivoRepository.findById(1L)).thenReturn(Optional.of(arquivoExistente));
        when(arquivoRepository.save(any(Arquivo.class))).thenReturn(arquivoExistente);

        // act
        arquivoService.salvarArquivo(mockMultipartFile, arquivoExistente);

        // assert
        verify(arquivoRepository).save(arquivoExistente);
        verify(arquivoRepository).findById(arquivoExistente.getId());
        assertThat(arquivoExistente.getNomeArquivo()).isEqualTo("documento.pdf");
        assertThat(arquivoExistente.getCaminhoArquivo()).isNotNull();
        assertThat(Files.exists(existingFilePath)).isFalse();
        assertThat(Files.exists(Paths.get(arquivoExistente.getCaminhoArquivo()))).isTrue();
    }

    @Test
    @DisplayName("Should throw exception when file is empty")
    void salvarArquivo_arquivoVazio() {
        // arrange
        mockMultipartFile = null;
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