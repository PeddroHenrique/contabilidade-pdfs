# Contabilidade PDFs

[![GitHub License](https://img.shields.io/github/license/PeddroHenrique/economista-pdfs)]() [![GitHub Release](https://img.shields.io/github/v/release/PeddroHenrique/economista-pdfs)]() [![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/PeddroHenrique/economista-pdfs/tests.yml)]() [![Tests](https://github.com/PeddroHenrique/economista-pdfs/actions/workflows/tests.yml/badge.svg)]()

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
- Banco de dados: todas as operações realizadas são persistidas no banco de dados.
- Docker: projeto capaz de rodar em containers docker.

## 📦 Pré-requisitos

- Docker -> https://www.docker.com/

## 🚀 Instalação

```
git pull https://github.com/PeddroHenrique/economista-pdfs
docker-compose up -d
```

## 💻 Uso

Após subir os containers docker, basta acessar http://localhost:8080/api/login

## 🛠️ Tecnologias

- java 17, Spring Boot MVC, Spring Security, Spring JPA;
- Thymeleaf, HTML e Bootstrap;
- PostgreSQL e Flyway;
- JUnit e Mockito;
- Git e Github;
- Docker;
- Swagger.
