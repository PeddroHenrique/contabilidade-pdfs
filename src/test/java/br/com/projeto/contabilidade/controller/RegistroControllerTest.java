package br.com.projeto.contabilidade.controller;

import br.com.projeto.contabilidade.dto.UserDto;
import br.com.projeto.contabilidade.model.User;
import br.com.projeto.contabilidade.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistroControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private RegistroController registroController;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto();
        userDto.setUsername("Usuario");
        userDto.setPassword("123");
        userDto.setConfirmPassword("123");
    }

    @Test
    @DisplayName("Should return the correct view")
    void registrarUsuario() {
        String viewName = registroController.registrarUsuario(model);

        assertThat(viewName).isEqualTo("registro");
        verify(model).addAttribute(anyString(), any(UserDto.class));
    }

    @Test
    @DisplayName("Should not save if the password don't match")
    void salvarUsuario_comErroDeValidacao() {
        // Arrange
        userDto.setConfirmPassword("senha_diferente");
        when(bindingResult.hasErrors()).thenReturn(true);

        // Act
        String viewName = registroController.salvarUsuario(userDto, bindingResult, model);

        // Assert
        assertThat(viewName).isEqualTo("registro");
        verify(bindingResult).rejectValue("confirmPassword", "error.user", "As senhas não conferem");
    }

    @Test
    @DisplayName("Should register the user successfully")
    void salvarUsuario_comSucessoNoRegistro() {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.registerUser(any(User.class))).thenReturn(user);

        String viewName = registroController.salvarUsuario(userDto, bindingResult, model);

        assertThat(viewName).isEqualTo("redirect:/login?registered");
        verify(userService).registerUser(any(User.class));
    }

    @Test
    @DisplayName("Should throw an exception")
    void SalvarUsuario_comExcecao() {
        when(bindingResult.hasErrors()).thenReturn(false);
        doThrow(new RuntimeException("Username já existe!")).when(userService).registerUser(any(User.class));

        String viewName = registroController.salvarUsuario(userDto, bindingResult, model);

        assertThat(viewName).isEqualTo("registro");
        verify(userService).registerUser(any(User.class));
        verify(model).addAttribute("errorMessage", "Username já existe!");
    }
}