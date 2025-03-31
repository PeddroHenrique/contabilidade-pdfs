package br.com.projeto.contabilidade.repository;

import br.com.projeto.contabilidade.model.Arquivo;
import br.com.projeto.contabilidade.model.Cliente;
import br.com.projeto.contabilidade.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
// ----Todas as 3 anotações têm o mesma função----
@SpringJUnitConfig
// @ExtendWith(SpringExtension.class)
// @SpringJUnitWebConfig
// ----ActiveProfiles (beans + properties) TestPropertySource (properties apenas)
// Se utilizado junto com o AUTO_CONFIGURED, não é neceário----
@TestPropertySource(locations = "classpath:application-test.properties")
// @ActiveProfiles("test")
// ----NONE caso seja o mesmo banco que o principal
// AUTO_CONFIGURED caso seja banco em memória (H2)----
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ArquivoRepositoryTest {

    @Autowired
    private ArquivoRepository arquivoRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {

        User user = criarUsuarioTest();
        LocalDateTime now = LocalDateTime.now();

        Cliente cliente1 = new Cliente();
        cliente1.setNome("Cliente1");
        cliente1.setUser(user);
        entityManager.persist(cliente1);

        Cliente cliente2 = new Cliente();
        cliente2.setNome("Cliente2");
        cliente2.setUser(user);
        entityManager.persist(cliente2);

        Arquivo arquivo1 = new Arquivo();
        arquivo1.setNomeArquivo("documento1.pdf");
        arquivo1.setCliente(cliente1);
        arquivo1.setDataCriacao(now.minusDays(4));
        entityManager.persist(arquivo1);

        Arquivo arquivo2 = new Arquivo();
        arquivo2.setNomeArquivo("documento2.pdf");
        arquivo2.setCliente(cliente1);
        arquivo2.setDataCriacao(now.minusDays(3));
        entityManager.persist(arquivo2);

        Arquivo arquivo3 = new Arquivo();
        arquivo3.setNomeArquivo("documento3.pdf");
        arquivo3.setCliente(cliente2);
        arquivo3.setDataCriacao(now.minusDays(2));
        entityManager.persist(arquivo3);

        Arquivo arquivo4 = new Arquivo();
        arquivo4.setNomeArquivo("documento4.pdf");
        arquivo4.setCliente(cliente2);
        arquivo4.setDataCriacao(now.minusDays(1));
        entityManager.persist(arquivo4);

        entityManager.flush();
    }

    private User criarUsuarioTest() {
        User user = new User();
        user.setUsername("Usuario Test");
        user.setPassword("Usuario123");
        user.setRole("USER");
        user.setEnabled(true);
        entityManager.persist(user);

        return user;
    }

    @Test
    void findArquivosByClienteUsuario_deveRetornarArquivosDoUsuario() {
        // arrange
        String username = "Usuario Test";
        Pageable pageable = PageRequest.of(0, 10);

        // act
        Page<Arquivo> resultado = arquivoRepository.findArquivosByClienteUsuario(username, pageable);

        // assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(4);
        assertThat(resultado.getContent())
                .extracting(Arquivo::getNomeArquivo)
                .containsExactly("documento4.pdf", "documento3.pdf", "documento2.pdf", "documento1.pdf");
    }

    @Test
    void findArquivosByClienteUsuario_comUsuarioInexistente_deveRetornarPaginaVazia() {
        String username = "Usuario Test Inexistente";
        Pageable pageable = PageRequest.of(0, 10);

        Page<Arquivo> resultado = arquivoRepository.findArquivosByClienteUsuario(username, pageable);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).isEmpty();
    }

    @Test
    void findArquivoByClienteNomeAndUsuario_deveRetornarArquivosFiltradosPorNomeEUsuario() {
        String username = "Usuario Test";
        String cliente = "Cliente1";
        Pageable pageable = PageRequest.of(0, 10);

        Page<Arquivo> resultado = arquivoRepository.findArquivosByClienteNomeAndUsuario(cliente, username, pageable);

        assertThat(resultado.getContent()).isNotNull();
        assertThat(resultado.getContent()).isNotEmpty();
        assertThat(resultado.getContent()).hasSize(2);
        assertThat(resultado.getContent())
                .extracting(Arquivo::getNomeArquivo)
                .containsExactly("documento2.pdf", "documento1.pdf");
    }

    @Test
    void findArquivoByClienteNomeAndUsuario_comClienteInexistente_deveRetonarPaginaVazia() {
        String username = "Usuario Test";
        String cliente = "Cliente Inexistente";
        Pageable pageable = PageRequest.of(0, 10);

        Page<Arquivo> resultado = arquivoRepository.findArquivosByClienteNomeAndUsuario(cliente, username, pageable);

        assertThat(resultado.getContent()).isNotNull();
        assertThat(resultado.getContent()).isEmpty();
    }

    @Test
    void findArquivoByClienteNomeAndUsuario_comUsuarioInexistente_deveRetornarPaginaVazia() {
        String username = "Usuario Test Inexistente";
        String cliente = "Cliente1";
        Pageable pageable = PageRequest.of(0, 10);

        Page<Arquivo> resultado = arquivoRepository.findArquivosByClienteNomeAndUsuario(cliente, username, pageable);

        assertThat(resultado.getContent()).isNotNull();
        assertThat(resultado.getContent()).isEmpty();
    }
}