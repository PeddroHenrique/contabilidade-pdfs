package br.com.projeto.contabilidade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class ContabilidadeApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContabilidadeApplication.class, args);
	}

}
