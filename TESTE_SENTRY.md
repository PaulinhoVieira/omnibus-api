# 🧪 Testando Integração com Sentry

A aplicação está rodando em: **http://localhost:8080**

## Dashboard do Sentry
Acesse: https://sentry.io → Seu projeto → Issues

---

## ✅ Testes para Capturar Erros no Sentry

### 1. **Teste de Login com Credenciais Inválidas** (401 - WARNING)

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "usuario@teste.com",
    "senha": "senhaerrada123"
  }'
```

**Erro esperado no Sentry:**
- **Tipo:** `BadCredentialsException`
- **Nível:** WARNING
- **Tags:** `error.type=authentication`, `auth.failure=bad_credentials`
- **Mensagem:** Bad credentials

---

### 2. **Teste de Validação de Dados** (400 - Não enviado ao Sentry)

```bash
curl -X POST http://localhost:8080/auth/cadastro \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "",
    "email": "emailinvalido",
    "cpf": "123"
  }'
```

**Resultado:** Erro de validação (400) - **NÃO** aparece no Sentry (evita poluição)

---

### 3. **Teste de Recurso Não Encontrado** (404 - WARNING)

```bash
curl -X GET http://localhost:8080/usuarios/99999
```

**Erro esperado no Sentry:**
- **Tipo:** `ResourceNotFoundException`
- **Nível:** WARNING
- **Mensagem:** Usuário não encontrado

---

### 4. **Teste de Upload de Arquivo Grande** (413 - Não enviado)

```bash
# Criar arquivo de 10MB (maior que o limite de 5MB)
dd if=/dev/zero of=/tmp/arquivo_grande.pdf bs=1M count=10

curl -X POST http://localhost:8080/documentos/upload \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -F "arquivo=@/tmp/arquivo_grande.pdf"
```

**Resultado:** Erro 413 - **NÃO** aparece no Sentry (erro esperado do cliente)

---

### 5. **Teste de Acesso Sem Autenticação** (401)

```bash
curl -X GET http://localhost:8080/usuarios
```

**Erro esperado no Sentry:**
- **Tipo:** `AuthenticationException`
- **Nível:** WARNING
- **Tags:** `error.type=authentication`

---

### 6. **Simulando Erro Interno (500 - ERROR)**

Para testar erros 500 capturados pelo handler global, você pode:

1. Desligar o banco PostgreSQL:
```bash
sudo systemctl stop postgresql
```

2. Tentar fazer login:
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "teste@teste.com",
    "senha": "senha123"
  }'
```

**Erro esperado no Sentry:**
- **Tipo:** `Exception` (genérico)
- **Nível:** ERROR
- **Tags:** `error.type=unhandled`, `http.status=500`

3. Religar o banco:
```bash
sudo systemctl start postgresql
```

---

## 📊 O que Verificar no Dashboard do Sentry

Depois de executar os testes, acesse o dashboard do Sentry e verifique:

1. **Stack Trace Completo:** Linha exata do erro
2. **Breadcrumbs:** Histórico de requisições HTTP antes do erro
3. **Tags Customizadas:**
   - `application=omnibus-api`
   - `module=authentication` / `storage` / etc
   - `error.type`, `http.status`, `auth.failure`
4. **Contexto Extra:**
   - Versão do Java
   - Sistema Operacional
   - URL da requisição
   - Método HTTP
5. **Release:** `0.0.1-SNAPSHOT`
6. **Environment:** `development`

---

## 🎯 Teste Rápido Recomendado

Execute este comando para gerar um erro de autenticação:

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"teste@erro.com","senha":"senhaerrada"}'
```

**Depois:**
1. Acesse https://sentry.io
2. Vá para o projeto Omnibus
3. Clique em "Issues"
4. Você verá o erro `BadCredentialsException` com todo o contexto!

---

## 🔄 Para Parar a Aplicação

Pressione `Ctrl + C` no terminal onde a aplicação está rodando.
