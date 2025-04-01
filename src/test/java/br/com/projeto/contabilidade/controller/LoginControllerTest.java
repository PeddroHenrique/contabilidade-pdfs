package br.com.projeto.contabilidade.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginControllerTest {

    @Mock
    private Model model;

    @InjectMocks
    private LoginController loginController;

    @Test
    @DisplayName("Should return the login page without any model")
    void login_semParametro() {
        String viewName = loginController.login(null, null, null, model);

        assertThat(viewName).isEqualTo("login");
        verifyNoInteractions(model);
    }

    @Test
    @DisplayName("Should return the login page with error model")
    void login_comErro() {
        String viewName = loginController.login("true", null, null, model);

        assertThat(viewName).isEqualTo("login");
        verify(model).addAttribute("error", "Nome do usuário ou senha inválidos!");
    }

    @Test
    @DisplayName("Should return the login page with logout model")
    void login_comLogout() {
        String viewName = loginController.login(null, "true", null, model);

        assertThat(viewName).isEqualTo("login");
        verify(model).addAttribute("message", "Você foi desconectado com sucesso!");
    }

    @Test
    @DisplayName("Should return the login page with registered model")
    void login_comRegistered() {
        String viewName = loginController.login(null, null, "true", model);

        assertThat(viewName).isEqualTo("login");
        verify(model).addAttribute("message", "Registro realizado com sucesso! Por favor, faça login.");
    }
}