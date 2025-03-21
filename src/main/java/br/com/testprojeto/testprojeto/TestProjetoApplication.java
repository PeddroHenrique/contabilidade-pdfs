package br.com.testprojeto.testprojeto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TestProjetoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TestProjetoApplication.class, args);
	}

}
