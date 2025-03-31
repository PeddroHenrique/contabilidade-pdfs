package br.com.testprojeto.testprojeto.service;

import br.com.testprojeto.testprojeto.model.User;
import br.com.testprojeto.testprojeto.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;



    @Test
    @DisplayName("Should create a UserDetails entity successfully")
    void loadUserByUsername_seUsuarioExistir() {
        // arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("Usuario");
        user.setPassword("usuario123");
        user.setRole("USER");
        user.setEnabled(true);

        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        // act
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());

        // assert
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(user.getUsername());
        assertThat(userDetails.getPassword()).isEqualTo("usuario123");
        assertThat(userDetails.isEnabled()).isEqualTo(true);
        assertThat(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))).isEqualTo(true);
        verify(userRepository, times(1)).findByUsername(user.getUsername());
    }

    @Test
    @DisplayName("Should throw an exception if the user doesn't exist")
    void loadUserByUsername_seUsuarioNaoExistir() {
        String usernameInexistente = "Usuario Inexistente";
        when(userRepository.findByUsername(usernameInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername(usernameInexistente))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("Usuário não encontrado: " + usernameInexistente);
        verify(userRepository, times(1)).findByUsername(usernameInexistente);
    }
}