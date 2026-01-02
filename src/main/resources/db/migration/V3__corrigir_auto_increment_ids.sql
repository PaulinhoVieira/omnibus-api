-- Migration V3: Corrigir auto-increment para os IDs

-- 1. Adicionar DEFAULT nas colunas id para usar as sequências
ALTER TABLE usuarios ALTER COLUMN id SET DEFAULT nextval('usuarios_id_seq');
ALTER TABLE empresas ALTER COLUMN id SET DEFAULT nextval('empresas_id_seq');
ALTER TABLE documentos ALTER COLUMN id SET DEFAULT nextval('documentos_id_seq');
ALTER TABLE viagens ALTER COLUMN id SET DEFAULT nextval('viagens_id_seq');
ALTER TABLE passagens ALTER COLUMN id SET DEFAULT nextval('passagens_id_seq');
