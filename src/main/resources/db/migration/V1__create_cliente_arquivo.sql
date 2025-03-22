CREATE TABLE IF NOT EXISTS cliente (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS arquivo (
    id BIGSERIAL PRIMARY KEY,
    apelido_arquivo VARCHAR(255),
    caminho_arquivo VARCHAR(255),
    nome_arquivo VARCHAR(255),
    data_criacao TIMESTAMP(6),
    data_atualizacao TIMESTAMP(6),
    cliente_id BIGINT
);

ALTER TABLE arquivo
ADD CONSTRAINT fk_cliente
FOREIGN KEY (cliente_id)
REFERENCES cliente(id);