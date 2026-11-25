package br.com.projeto.contabilidade.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testa as regras de segurança com a aplicação inteira carregada
 * (perfil "test": banco H2 em memória e usuário teste/teste).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTest {

    private final MockMvc mockMvc;

    @Autowired
    public SecurityConfigTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void paginaDeLogin_deveSerPublica() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    void paginaProtegida_semLogin_deveRedirecionarParaLogin() throws Exception {
        mockMvc.perform(get("/arquivos"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void login_comSenhaCorreta_deveIrParaListaDeArquivos() throws Exception {
        mockMvc.perform(formLogin("/login").user("teste").password("teste"))
                .andExpect(redirectedUrl("/arquivos"));
    }

    @Test
    void login_comSenhaErrada_deveVoltarComErro() throws Exception {
        mockMvc.perform(formLogin("/login").user("teste").password("errada"))
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    @WithMockUser
    void post_semTokenCsrf_deveSerRecusado() throws Exception {
        mockMvc.perform(post("/clientes").param("nome", "Cliente"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    void post_comTokenCsrf_deveSerAceito() throws Exception {
        mockMvc.perform(post("/clientes").param("nome", "Cliente").with(csrf()))
                .andExpect(redirectedUrl("/clientes"));
    }

    @Test
    @WithMockUser

    void logout_deveRedirecionarParaLoginComMensagem() throws Exception {
        mockMvc.perform(logout("/logout"))
                .andExpect(redirectedUrl("/login?logout"));
    }
}