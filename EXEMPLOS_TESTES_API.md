# Exemplos de Testes das Rotas da API Ônibus

## 🔐 USUÁRIOS

### 1️⃣ CREATE - Criar novo usuário
```http
POST http://localhost:8080/usuario
Content-Type: application/json

{
  "nome": "João Silva",
  "email": "joao.silva@email.com",
  "senha": "Senha@123",
  "cpf": "86538397042"
}
```

**Resposta esperada (201 Created):**
```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao.silva@email.com",
  "cpf": "86538397042"
}
```

---

### 2️⃣ CREATE - Segundo usuário (para associar à empresa)
```http
POST http://localhost:8080/usuario
Content-Type: application/json

{
  "nome": "Maria Santos",
  "email": "maria.santos@email.com",
  "senha": "Senha@456",
  "cpf": "93463633043"
}
```

**Resposta esperada (201 Created):**
```json
{
  "id": 2,
  "nome": "Maria Santos",
  "email": "maria.santos@email.com",
  "cpf": "93463633043"
}
```

---

### 3️⃣ READ - Buscar usuário por ID
```http
GET http://localhost:8080/usuario/1
```

**Resposta esperada (200 OK):**
```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao.silva@email.com",
  "cpf": "86538397042"
}
```

---

### 4️⃣ UPDATE - Atualizar usuário
```http
PUT http://localhost:8080/usuario/1
Content-Type: application/json

{
  "nome": "João Silva Atualizado",
  "email": "joao.silva.novo@email.com",
  "senha": "NovaSenha@123",
  "cpf": "86538397042"
}
```

**Resposta esperada (200 OK):**
```json
{
  "id": 1,
  "nome": "João Silva Atualizado",
  "email": "joao.silva.novo@email.com",
  "cpf": "86538397042"
}
```

---

### 5️⃣ DELETE - Deletar usuário
```http
DELETE http://localhost:8080/usuario/1
```

**Resposta esperada (204 No Content)**

---

## 🏢 EMPRESAS

### 1️⃣ CREATE - Criar nova empresa
```http
POST http://localhost:8080/empresa
Content-Type: application/json

{
  "cnpj": "96800579000146",
  "nomeFantasia": "Ônibus do Brasil",
  "razaoSocial": "Empresa de Transportes Brasil Ltda",
  "usuarioDonoId": 2
}
```

**Resposta esperada (201 Created):**
```json
{
  "id": 1,
  "cnpj": "96800579000146",
  "nomeFantasia": "Ônibus do Brasil",
  "razaoSocial": "Empresa de Transportes Brasil Ltda",
  "usuarioDonoId": 2
}
```

---

### 2️⃣ CREATE - Segunda empresa
```http
POST http://localhost:8080/empresa
Content-Type: application/json

{
  "cnpj": "02776174000176",
  "nomeFantasia": "Passagens Rápidas",
  "razaoSocial": "Passagens Rápidas Transportes Ltda",
  "usuarioDonoId": 2
}
```

**Resposta esperada (201 Created):**
```json
{
  "id": 2,
  "cnpj": "02776174000176",
  "nomeFantasia": "Passagens Rápidas",
  "razaoSocial": "Passagens Rápidas Transportes Ltda",
  "usuarioDonoId": 2
}
```

---

### 3️⃣ READ - Buscar empresa por ID
```http
GET http://localhost:8080/empresa/1
```

**Resposta esperada (200 OK):**
```json
{
  "id": 1,
  "cnpj": "96800579000146",
  "nomeFantasia": "Ônibus do Brasil",
  "razaoSocial": "Empresa de Transportes Brasil Ltda",
  "usuarioDonoId": 2
}
```

---

### 4️⃣ UPDATE - Atualizar empresa
```http
PUT http://localhost:8080/empresa/1
Content-Type: application/json

{
  "cnpj": "96800579000146",
  "nomeFantasia": "Ônibus do Brasil - Filial SP",
  "razaoSocial": "Empresa de Transportes Brasil Ltda - Filial SP",
  "usuarioDonoId": 2
}
```

**Resposta esperada (200 OK):**
```json
{
  "id": 1,
  "cnpj": "96800579000146",
  "nomeFantasia": "Ônibus do Brasil - Filial SP",
  "razaoSocial": "Empresa de Transportes Brasil Ltda - Filial SP",
  "usuarioDonoId": 2
}
```

---

### 5️⃣ DELETE - Deletar empresa
```http
DELETE http://localhost:8080/empresa/1
```

**Resposta esperada (204 No Content)**

---

## 📋 Ordem Recomendada para Testar

1. **Criar usuário 1** (João Silva)
2. **Criar usuário 2** (Maria Santos) - será o dono das empresas
3. **Buscar usuário 1** - para verificar o GET
4. **Atualizar usuário 1**
5. **Criar empresa 1** (usa ID do usuário 2)
6. **Criar empresa 2** (usa ID do usuário 2)
7. **Buscar empresa 1** - para verificar o GET
8. **Atualizar empresa 1**
9. **Deletar empresa 1**
10. **Deletar empresa 2**
11. **Deletar usuário 1**
12. **Deletar usuário 2**

---

## ⚙️ Validações Esperadas

### Usuário
- ✅ **Nome**: Obrigatório, não pode ser vazio
- ✅ **Email**: Obrigatório, deve ser um email válido
- ✅ **Senha**: Obrigatória, não pode ser vazia
- ✅ **CPF**: Obrigatório, deve ser CPF válido (ex: 12345678901 é um CPF fake válido)

### Empresa
- ✅ **CNPJ**: Obrigatório, deve ser CNPJ válido (ex: 11222333000181 é um CNPJ fake válido)
- ✅ **Nome Fantasia**: Obrigatório, não pode ser vazio
- ✅ **Razão Social**: Opcional
- ✅ **ID do Usuário Dono**: Obrigatório, deve ser um usuário existente

---

## 🧪 Testes com Erros

### Teste: Email inválido
```http
POST http://localhost:8080/usuario
Content-Type: application/json

{
  "nome": "Teste Erro",
  "email": "email-invalido",
  "senha": "Senha@123",
  "cpf": "12345678901"
}
```

### Teste: CPF inválido
```http
POST http://localhost:8080/usuario
Content-Type: application/json

{
  "nome": "Teste Erro",
  "email": "teste@email.com",
  "senha": "Senha@123",
  "cpf": "00000000000"
}
```

### Teste: CNPJ inválido
```http
POST http://localhost:8080/empresa
Content-Type: application/json

{
  "cnpj": "00000000000000",
  "nomeFantasia": "Teste",
  "razaoSocial": "Teste",
  "usuarioDonoId": 2
}
```

### Teste: Usuário dono não existe
```http
POST http://localhost:8080/empresa
Content-Type: application/json

{
  "cnpj": "11222333000181",
  "nomeFantasia": "Teste",
  "razaoSocial": "Teste",
  "usuarioDonoId": 999
}
```

---

## 🌐 URLs Base
- **Desenvolvimento**: `http://localhost:8080`
- **Documentação Swagger**: `http://localhost:8080/swagger-ui.html`
