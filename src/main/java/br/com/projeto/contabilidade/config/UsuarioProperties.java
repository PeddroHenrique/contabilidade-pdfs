package br.com.projeto.contabilidade.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.usuario")
public record UsuarioProperties (
        @NotBlank String login,
        @NotBlank String senha) {

}
