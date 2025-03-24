package br.com.testprojeto.testprojeto.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserDto {

    @NotBlank(message = "Usuário é obrigatório")
    @Size(min = 3, max = 20, message = "Usuário deve ter entre 3 e 20 caracteres")
    private String username;

    @NotBlank(message = "Senha é obrigatória")
    @Size(max = 15, message = "Senha deve ter no máximo 15 caracteres")
    private String password;

    @NotBlank(message = "Confirmação de senha é obrigatória")
    private String confirmPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
