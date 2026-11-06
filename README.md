# Contabilidade PDFs

[![GitHub License](https://img.shields.io/github/license/PeddroHenrique/contabilidade-pdfs)]() [![GitHub Release](https://img.shields.io/github/v/release/PeddroHenrique/contabilidade-pdfs)]() [![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/PeddroHenrique/contabilidade-pdfs/tests.yml)]() [![Tests](https://github.com/PeddroHenrique/contabilidade-pdfs/actions/workflows/tests.yml/badge.svg)]()

**Atividade desenvolvida para atender a necessidade de uma economista. Realiza a criação, listagem, edição e remoção de clientes e seus respectivos arquivos (PDFs). Cada usuário criado tem sua própria lista de clientes e arquivos relacionados.**

## 📌 Tabela de Conteúdos

- [Funcionalidades](#✨-funcionalidades)
- [Pré-requisitos](#📦-pré-requisitos)
- [Instalação](#🚀-instalação)
- [Uso](#💻-uso)
- [Tecnologias](#🛠️-tecnologias)

## ✨ Funcionalidades

- Gerenciamento de arquivos: cada usuário só é capaz de gerenciar seus próprios clientes e arquivos relacionados, não interferindo nos dos demais.
- Listagem dos arquivos: é capaz de listar todos os arquivos relacionado a um usuário ou a um determinado cliente.
- Paginação: reduz o consumo de recursos e melhora o desempenho.
- Authenticação: realiza o salvamento de usuários no banco de dados, utilizando boas práticas de encriptação.
- Banco de dados: todas as operações são persistidas em um banco H2 embutido, sem precisar instalar um servidor de banco.

## 📦 Pré-requisitos

- Java 21 (JDK) -> https://adoptium.net/

## 🚀 Executando em desenvolvimento

```bash
git clone https://github.com/PeddroHenrique/contabilidade-pdfs.git
cd contabilidade-pdfs
./mvnw spring-boot:run
```

O comando acima ativa o perfil `dev`: o banco e os PDFs ficam na pasta `dados-dev/` do projeto.

Para rodar os testes:

```bash
./mvnw verify
```

## 💻 Uso

Com a aplicação rodando, acesse http://localhost:8080/api/login

Fora do perfil `dev`, os dados ficam em `%LOCALAPPDATA%\ContabilidadePDFs` (Windows) ou em `~/ContabilidadePDFs` (outros sistemas).

## 🛠️ Tecnologias

- Java 21, Spring Boot 4 (MVC), Spring Security, Spring Data JPA;
- Thymeleaf, HTML e Bootstrap;
- H2 (modo arquivo) e Flyway;
- JUnit e Mockito;
- Git, GitHub e GitHub Actions;
- Swagger.
