package br.com.testprojeto.testprojeto.service;

import br.com.testprojeto.testprojeto.model.User;
import br.com.testprojeto.testprojeto.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("Usuario Test");
        user.setPassword("usuario123");
        user.setRole("USER");
        user.setEnabled(true);
    }

    @Test
    @DisplayName("Should return a user based on the username")
    void listarPorUsername_seExistir() {
        // arrange
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        // act
        User resultado = userService.listarPorUsername(user.getUsername());

        // assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getUsername()).isEqualTo("Usuario Test");
        assertThat(resultado.getPassword()).isEqualTo(user.getPassword());
        assertThat(resultado.getRole()).isEqualTo("USER");
        assertThat(resultado.isEnabled()).isEqualTo(true);
        verify(userRepository, times(1)).findByUsername(user.getUsername());
    }

    @Test
    @DisplayName("Should throw an exception if the id doesn't exist")
    void listarPorUsername_seNaoExistir() {
        // arrange
        when(userRepository.findByUsername("Usuario Inexistente")).thenReturn(Optional.empty());

        // act assert
        assertThatThrownBy(() -> userService.listarPorUsername("Usuario Inexistente"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Usuário não encontrado");
    }

    @Test
    @DisplayName("Should register a new user if it doens't already exist")
    void registerUser_novoUsuario() {
        // arrange
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        // act
        User resultado = userService.registerUser(user);

        // assert
        assertThat(resultado).isNotNull();
        assertThat(resultado.getUsername()).isEqualTo("Usuario Test");
        verify(userRepository, times(1)).existsByUsername(user.getUsername());
    }

    @Test
    @DisplayName("Should throw an exception if the username already exist")
    void registerUser_usuarioExistente() {
        // arrange
        when(userRepository.existsByUsername(user.getUsername())).thenReturn(true);

        // act assert
        assertThatThrownBy(() -> userService.registerUser(user))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Username já existe!");
    }
}