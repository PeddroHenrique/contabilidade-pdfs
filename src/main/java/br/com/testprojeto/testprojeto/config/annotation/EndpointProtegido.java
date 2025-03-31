package br.com.testprojeto.testprojeto.config.annotation;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = """
                Caso 1: Operação concluída com sucesso.
                \nCaso 2: Usuários não autenticados serão redirecionados para página de login.
                """)
})
public @interface EndpointProtegido {
}
