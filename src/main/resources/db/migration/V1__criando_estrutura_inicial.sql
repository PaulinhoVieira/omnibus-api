-- 1. Tabela Principal de Usuários (Pessoa Física)
CREATE TABLE usuarios (
    id UUID PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    senha VARCHAR(255) NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Perfis de Acesso (Roles)
CREATE TABLE perfis_usuario (
    usuario_id UUID NOT NULL,
    perfil VARCHAR(50) NOT NULL, -- 'PASSAGEIRO', 'EMPRESA', 'ADMIN'
    PRIMARY KEY (usuario_id, perfil),
    CONSTRAINT fk_perfil_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- 3. Tabela de Empresas (Pessoa Jurídica vinculada a um Usuário)
CREATE TABLE empresas (
    id UUID PRIMARY KEY,
    usuario_dono_id UUID NOT NULL,
    cnpj VARCHAR(18) UNIQUE NOT NULL,
    nome_fantasia VARCHAR(255) NOT NULL,
    razao_social VARCHAR(255),
    CONSTRAINT fk_usuario_dono FOREIGN KEY (usuario_dono_id) REFERENCES usuarios(id)
);

-- 4. Gestão de Documentos (Metadados para o MinIO)
CREATE TABLE documentos (
    id UUID PRIMARY KEY,
    usuario_id UUID NOT NULL,
    tipo_documento VARCHAR(50) NOT NULL, -- 'RG', 'CARTEIRA_ESTUDANTE', 'CNPJ_DOC'
    nome_arquivo_minio VARCHAR(255) NOT NULL, -- Nome único gerado para o bucket
    content_type VARCHAR(100),
    validado BOOLEAN DEFAULT FALSE,
    data_upload TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_documento_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- 5. Viagens
CREATE TABLE viagens (
    id UUID PRIMARY KEY,
    empresa_id UUID NOT NULL,
    origem VARCHAR(100) NOT NULL,
    destino VARCHAR(100) NOT NULL,
    data_partida TIMESTAMP NOT NULL,
    preco DECIMAL(10,2) NOT NULL,
    vagas_totais INTEGER NOT NULL,
    vagas_disponiveis INTEGER NOT NULL,
    possui_assento_marcado BOOLEAN DEFAULT TRUE, -- Flag para lógica de poltrona
    CONSTRAINT fk_viagem_empresa FOREIGN KEY (empresa_id) REFERENCES empresas(id)
);

-- 6. Passagens (Vendas)
CREATE TABLE passagens (
    id UUID PRIMARY KEY,
    viagem_id UUID NOT NULL,
    passageiro_id UUID NOT NULL,
    data_compra TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL, -- 'PENDENTE', 'PAGA', 'CANCELADA'
    poltrona INTEGER, -- opcional
    valor_pago DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_passagem_viagem FOREIGN KEY (viagem_id) REFERENCES viagens(id),
    CONSTRAINT fk_passagem_passageiro FOREIGN KEY (passageiro_id) REFERENCES usuarios(id)
);