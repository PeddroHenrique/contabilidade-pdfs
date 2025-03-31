package br.com.testprojeto.testprojeto.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@OpenAPIDefinition(
//        info = @Info(
//                title = "OpenApi Specification - Gerenciador de PDFs",
//                description = "Saves PDFs for each client",
//                contact = @Contact(
//                        name = "Pedro Henrique Brandão Santos"
//                ),
//                version = "1.0"
//        ),
//        servers = @Server(
//                description = "Local ENV",
//                url = "http://localhost:8080"
//        )
//)
//@SecurityScheme(
//        name = "BasicAuth",
//        description = "Basic authentication method",
//        scheme = "Basic",
//        type = SecuritySchemeType.HTTP
//)
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
                .info(new Info()
                        .title("Aplicação MVC Spring Boot - Gerenciador de PDFs")
                        .description("Saves PDFs for each client")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Pedro Henrique Brandão Santos")
                                .email("pedro080-henrique@hotmail.com")))
                // usar quando -> Login por formulário
                .addSecurityItem(new SecurityRequirement().addList("cookieAuth"))
                .components(new Components()
                        .addSecuritySchemes("cookieAuth",
                                new SecurityScheme()
                                        .name("cookieAuth")
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.COOKIE)
                                        .scheme("jsessionid")))
                .addTagsItem(new Tag().name("Autenticação").description("Endpoints de login e registro"));
                // usar quando -> Login por usuário em memória
//                .addSecurityItem(new SecurityRequirement().addList("basicAuth"))
//                .components(new Components()
//                        .addSecuritySchemes("basicAuth",
//                                new SecurityScheme()
//                                        .name("basicAuth")
//                                        .type(SecurityScheme.Type.HTTP)
//                                        .scheme("basic")));
    }
}
