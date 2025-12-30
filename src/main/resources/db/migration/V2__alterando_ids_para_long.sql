-- Migration V2: Alterando IDs de UUID para BIGINT (LONG)

-- 1. Remover constraints de chave estrangeira
ALTER TABLE perfis_usuario DROP CONSTRAINT IF EXISTS fk_perfil_usuario;
ALTER TABLE empresas DROP CONSTRAINT IF EXISTS fk_usuario_dono;
ALTER TABLE documentos DROP CONSTRAINT IF EXISTS fk_documento_usuario;
ALTER TABLE viagens DROP CONSTRAINT IF EXISTS fk_viagem_empresa;
ALTER TABLE passagens DROP CONSTRAINT IF EXISTS fk_passagem_viagem;
ALTER TABLE passagens DROP CONSTRAINT IF EXISTS fk_passagem_passageiro;

-- 2. Alterar tabela passagens (começando pelas dependentes)
ALTER TABLE passagens DROP CONSTRAINT IF EXISTS passagens_pkey;
ALTER TABLE passagens ALTER COLUMN id TYPE BIGINT USING id::text::bigint;
ALTER TABLE passagens ALTER COLUMN viagem_id TYPE BIGINT USING viagem_id::text::bigint;
ALTER TABLE passagens ALTER COLUMN passageiro_id TYPE BIGINT USING passageiro_id::text::bigint;
ALTER TABLE passagens ADD PRIMARY KEY (id);

-- 3. Alterar tabela viagens
ALTER TABLE viagens DROP CONSTRAINT IF EXISTS viagens_pkey;
ALTER TABLE viagens ALTER COLUMN id TYPE BIGINT USING id::text::bigint;
ALTER TABLE viagens ALTER COLUMN empresa_id TYPE BIGINT USING empresa_id::text::bigint;
ALTER TABLE viagens ADD PRIMARY KEY (id);

-- 4. Alterar tabela documentos
ALTER TABLE documentos DROP CONSTRAINT IF EXISTS documentos_pkey;
ALTER TABLE documentos ALTER COLUMN id TYPE BIGINT USING id::text::bigint;
ALTER TABLE documentos ALTER COLUMN usuario_id TYPE BIGINT USING usuario_id::text::bigint;
ALTER TABLE documentos ADD PRIMARY KEY (id);

-- 5. Alterar tabela empresas
ALTER TABLE empresas DROP CONSTRAINT IF EXISTS empresas_pkey;
ALTER TABLE empresas ALTER COLUMN id TYPE BIGINT USING id::text::bigint;
ALTER TABLE empresas ALTER COLUMN usuario_dono_id TYPE BIGINT USING usuario_dono_id::text::bigint;
ALTER TABLE empresas ADD PRIMARY KEY (id);

-- 6. Alterar tabela perfis_usuario
ALTER TABLE perfis_usuario DROP CONSTRAINT IF EXISTS perfis_usuario_pkey;
ALTER TABLE perfis_usuario ALTER COLUMN usuario_id TYPE BIGINT USING usuario_id::text::bigint;
ALTER TABLE perfis_usuario ADD PRIMARY KEY (usuario_id, perfil);

-- 7. Alterar tabela usuarios (última, pois é referenciada por muitas)
ALTER TABLE usuarios DROP CONSTRAINT IF EXISTS usuarios_pkey;
ALTER TABLE usuarios ALTER COLUMN id TYPE BIGINT USING id::text::bigint;
ALTER TABLE usuarios ADD PRIMARY KEY (id);

-- 8. Recriar constraints de chave estrangeira
ALTER TABLE perfis_usuario 
    ADD CONSTRAINT fk_perfil_usuario 
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE;

ALTER TABLE empresas 
    ADD CONSTRAINT fk_usuario_dono 
    FOREIGN KEY (usuario_dono_id) REFERENCES usuarios(id);

ALTER TABLE documentos 
    ADD CONSTRAINT fk_documento_usuario 
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id);

ALTER TABLE viagens 
    ADD CONSTRAINT fk_viagem_empresa 
    FOREIGN KEY (empresa_id) REFERENCES empresas(id);

ALTER TABLE passagens 
    ADD CONSTRAINT fk_passagem_viagem 
    FOREIGN KEY (viagem_id) REFERENCES viagens(id);

ALTER TABLE passagens 
    ADD CONSTRAINT fk_passagem_passageiro 
    FOREIGN KEY (passageiro_id) REFERENCES usuarios(id);

-- 9. Criar sequências para auto-incremento (caso necessário)
CREATE SEQUENCE IF NOT EXISTS usuarios_id_seq;
CREATE SEQUENCE IF NOT EXISTS empresas_id_seq;
CREATE SEQUENCE IF NOT EXISTS documentos_id_seq;
CREATE SEQUENCE IF NOT EXISTS viagens_id_seq;
CREATE SEQUENCE IF NOT EXISTS passagens_id_seq;

-- 10. Ajustar próximo valor das sequências (se houver dados)
SELECT setval('usuarios_id_seq', COALESCE((SELECT MAX(id) FROM usuarios), 0) + 1, false);
SELECT setval('empresas_id_seq', COALESCE((SELECT MAX(id) FROM empresas), 0) + 1, false);
SELECT setval('documentos_id_seq', COALESCE((SELECT MAX(id) FROM documentos), 0) + 1, false);
SELECT setval('viagens_id_seq', COALESCE((SELECT MAX(id) FROM viagens), 0) + 1, false);
SELECT setval('passagens_id_seq', COALESCE((SELECT MAX(id) FROM passagens), 0) + 1, false);
