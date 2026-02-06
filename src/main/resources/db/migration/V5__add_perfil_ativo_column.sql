-- Adicionar coluna de perfil ativo na tabela usuarios
ALTER TABLE usuarios ADD COLUMN perfil_ativo VARCHAR(50) NOT NULL DEFAULT 'PASSAGEIRO';
