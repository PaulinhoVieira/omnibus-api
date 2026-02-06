# Arquitetura do Projeto - Omnibus API

## 📋 Visão Geral

Este documento descreve a organização da arquitetura do projeto **Omnibus API**, um sistema de gerenciamento de passagens de ônibus desenvolvido com Spring Boot 3.5.10 e Java 21.

---

## 🏗️ Estrutura de Pastas

```
omnibus-api/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/com/vendas/passagem/omnibus/
│   │   │       ├── OmnibusApiApplication.java          # Classe principal da aplicação
│   │   │       ├── annotation/                         # Anotações customizadas
│   │   │       ├── config/                             # Configurações da aplicação
│   │   │       ├── controller/                         # Controllers REST
│   │   │       ├── domain/                             # Entidades do domínio
│   │   │       ├── dto/                                # Objetos de Transferência de Dados
│   │   │       ├── exception/                          # Tratamento de exceções
│   │   │       ├── repository/                         # Camada de persistência
│   │   │       └── service/                            # Lógica de negócio
│   │   │
│   │   └── resources/
│   │       ├── application.properties                  # Configurações da aplicação
│   │       ├── db/migration/                           # Scripts de migração Flyway
│   │       ├── static/                                 # Arquivos estáticos
│   │       └── templates/                              # Templates (se aplicável)
│   │
│   └── test/
│       └── java/                                       # Testes unitários e integração
│
├── target/                                             # Artefatos compilados (não versionado)
├── docker-compose.yml                                  # Configuração Docker
├── pom.xml                                             # Configuração Maven
├── HELP.md                                             # Documentação de ajuda
├── EXEMPLOS_TESTES_API.md                             # Exemplos de testes da API
└── ARQUITETURA.md                                      # Este documento
```

---

## 📦 Detalhamento das Camadas

### 1. **annotation/** - Anotações Customizadas
Contém anotações personalizadas utilizadas no projeto para marcar entidades e métodos.

```
annotation/
└── Auditable.java                                      # Anotação para marcar entidades auditáveis
```

**Arquivos:**
- **Auditable.java:** Anotação customizada que marca classes de entidade para auditoria automática com Hibernate Envers

**Responsabilidade:** Definir anotações reutilizáveis para aspectos transversais como auditoria, validação e segurança.

---

### 2. **config/** - Configurações
Configurações técnicas e integrações da aplicação, organizada por domínio funcional.

```
config/
├── audit/
│   └── AuditAspect.java                               # Aspecto AOP para auditoria
├── minio/
│   └── MinioConfig.java                               # Configuração cliente MinIO
└── security/
    ├── SecurityConfigurations.java                    # Configurações Spring Security
    ├── TokenFilter.java                               # Filtro JWT
    └── TokenService.java                              # Serviço de geração/validação tokens
```

**Subpastas e Responsabilidades:**

#### **config/audit/**
- **AuditAspect.java:** Implementa aspect AOP para auditoria automática de operações em entidades marcadas com `@Auditable`. Integra-se com Hibernate Envers para registrar todas as mudanças.

#### **config/minio/**
- **MinioConfig.java:** Bean de configuração do cliente MinIO. Estabelece conexão com servidor MinIO para armazenamento de documentos e arquivos.

#### **config/security/**
- **SecurityConfigurations.java:** Configuração centralizada do Spring Security (autenticação, autorização, CORS, HTTPS)
- **TokenService.java:** Serviço responsável por geração, validação e renovação de tokens JWT
- **TokenFilter.java:** Filtro customizado que intercepta requisições e valida tokens JWT

**Responsabilidade:** 
- Configuração de segurança e autenticação baseada em JWT
- Integração com serviços externos (MinIO) para armazenamento em nuvem
- Auditoria automática com Hibernate Envers e AOP

```
domain/
├── Documento.java                                      # Entidade Documento
├── Empresa.java                                        # Entidade Empresa
├── Passagen.java                                       # Entidade Passagem
├── PerfilUsuarioId.java                               # Chave composta (PK) perfil/usuário
├── PerfisUsuario.java                                 # Entidade Perfis de Usuário
├── Usuario.java                                        # Entidade Usuário
├── Viagen.java                                         # Entidade Viagem
├── audit/
│   └── AuditLog.java                                  # Entidade de logs de auditoria
└── enums/
    ├── Status.java                                    # Enum com status (ATIVO, INATIVO, etc)
    ├── TipoDocumento.java                             # Enum tipo de documento (CPF, CNPJ, RG)
    └── TipoPerfil.java                                # Enum perfil de usuário (ADMIN, USER, etc)
```

**Entidades Principais:**
- **Documento.java:** Armazena informações de documentação de usuários/empresas
- **Empresa.java:** Representa empresa transportadora de ônibus
- **Passagen.java:** Representa uma passagem de ônibus vendida
- **Usuario.java:** Representa um usuário do sistema
- **Viagen.java:** Representa uma viagem/trajeto disponível
- **PerfisUsuario.java:** Associa perfis de acesso aos usuários
- **PerfilUsuarioId.java:** Chave primária composta para a entidade PerfisUsuario

**Subpastas:**

#### **domain/audit/**
- **AuditLog.java:** Entidade que armazena histórico de todas as mudanças nas entidades auditadas. Trabalhando em conjunto com Hibernate Envers, mantém registro completo de versões anteriores.

#### **domain/enums/**
Enumerações que representam valores fixos de domínio:
- **Status.java:** Define estados possíveis de registros (ex: ATIVO, INATIVO, SUSPENSO)
- **TipoDocumento.java:** Define tipos de documentos válidos (CPF, CNPJ, RG, CNH)
- **TipoPerfil.java:** Define papéis de usuários no sistema (ADMINISTRADOR, GERENTE, USUARIO)

**Responsabilidade:** 
- Modelar o domínio da aplicação com precisão
- Mapeamento ORM JPA/Hibernate com anotações
- Validações em nível de domínio
- Relacionamentos entre entidades (1:N, M:N)
- Encapsulamento de lógica de identidade e estado
### 4. **domain/** - Entidades do Domínio, separados por propósito (request/response) com mapeadores customizados.

```
dto/
├── mapper/
│   ├── EmpresaMapper.java                             # Mapeador Empresa ↔ EmpresaDTO
│   └── UsuarioMapper.java                             # Mapeador Usuario ↔ UsuarioDTO
├── request/
│   ├── AuthenticationDTO.java                         # Dados para login (usuario + senha)
│   ├── EmpresaRequestDTO.java                         # Dados de entrada para criar/atualizar empresa
│   └── UsuarioRequestDTO.java                         # Dados de entrada para criar/atualizar usuário
└── response/
    ├── DocumentoResponseDTO.java                      # Dados de saída para documento
    ├── EmpresaResponseDTO.java                        # Dados de saída para empresa
    ├── ErrorResponseDTO.java                          # Resposta padrão de erro
    └── UsuarioResponseDTO.java                        # Dados de saída para usuário
```

**Subpastas e Responsabilidades:**

#### **dto/mapper/**
Mapeadores que convertem entre entidades de domínio e DTOs:
- **EmpresaMapper.java:** Converte Entity Empresa ↔ EmpresaRequestDTO/EmpresaResponseDTO
- **UsuarioMapper.java:** Converte Entity Usuario ↔ UsuarioRequestDTO/UsuarioResponseDTO
- Implementam padrão Builder ou ModelMapper para conversão

#### **dto/request/**
DTOs de entrada que recebem dados do cliente:
- **AuthenticationDTO.java:** Contém credenciais para login (username, password)
- **EmpresaRequestDTO.java:** Recebe dados para criação/atualização de empresa (nome, CNPJ, etc)
- **UsuarioRequestDTO.java:** Recebe dados para criação/atualização de usuário (nome, email, etc)
- Incluem validações com anotações do Jakarta Validation (@NotNull, @Email, @Size, etc)

#### **dto/response/**
DTOs de saída que retornam dados ao cliente:
- **DocumentoResponseDTO.java:** Retorna dados públicos de documento (ID, tipo, número)
- **EmpresaResponseDTO.java:** Retorna informações públicas de empresa (ID, nome, CNPJ, status)
- **UsuarioResponseDTO.java:** Retorna informações públicas de usuário (ID, nome, email, perfil)
- **ErrorResponseDTO.java:** Resposta padronizada para erros (código, mensagem, timestamp)
- Nunca expõem informações sensíveis (senhas, tokens internos)

**Responsabilidade:** 
- Controlar dados expostos pela API REST
- Separar modelo de domínio da camada  e handler global para tratamento de erros.

```
exception/
├── BusinessException.java                             # Exceção genérica de negócio
├── DocumentoUploadException.java                      # Exceção para falha em upload
├── DuplicateResourceException.java                    # Exceção para duplicação de recurso
├── FileValidationException.java                       # Exceção para validação de arquivo
├── GlobalExceptionHandler.java                        # Handler global de exceções
├── InvalidDtoException.java                           # Exceção para DTO inválido
├── MinioStorageException.java                         # Exceção de armazenamento MinIO
├── ResourceNotFoundException.java                     # Exceção para recurso não encontrado
└── TokenGenerationException.java                      # Exceção para falha em geração de token
```

**Exceções Customizadas:**
- **BusinessException.java:** Exceção base para violações de regras de negócio (ex: passagem já vendida)
- **ResourceNotFoundException.java:** Lançada quando um recurso solicitado não existe (HTTP 404)
- **DuplicateResourceException.java:** Lançada ao tentar criar recurso duplicado (ex: dois usuários com mesmo email)
- **InvalidDtoException.java:** Lançada quando dados de entrada não passam em validação (HTTP 400)
- **DocumentoUploadException.java:** Lançada quando falha upload de documento
- **FileValidationException.java:** Lançada quando arquivo não atende critérios (tipo, tamanho, etc)
- **MinioStorageException.java:** Lançada quando há erro ao comunicar com MinIO
- **TokenGenerationException.java:** Lançada quando há falha na geração ou validação de tokens JWT

**Handler Global:**
- **GlobalExceptionHandler.java:** Classe anotada com `@RestControllerAdvice` que centraliza tratamento de todas as exceções
  - Mapeia exceções para respostas HTTP padronizadas
  - Formata response com ErrorResponseDTO
  - Define status HTTP apropriados (400, 404, 500, etc)
  - Adiciona informações úteis ao cliente (mensagem, timestamp, path)

**Responsabilidade:** 
- Definir exceções de negócio específicas do domínio
- Tratamento global e centralizado de erros
- Padronização de respostas de erro com ErrorResponseDTO
- Logging de erros críticos
- Mapeamento de exceções para códigos HTTP apropriados

---

### 5. **dto/** - Data Transfer Objects
Objetos para transferência de dados entre camadas.

```
dto/
├── mapper/                                             # Conversores entre Domain e DTO
├── request/                                            # DTOs de entrada (requisições)
└── response/                                           # DTOs de saída (respostas)
```

**Responsabilidade:** 
- Controlar dados expostos pela API
- Separar modelo de domínio da camada de apresentação
- Facilitar versionamento da API
- Validações de entrada

---

### 6. **exception/** - Tratamento de Exceções
Gerenciamento centralizado de exceções.

```
exception/
└── [Classes de exceções customizadas]
```

**Responsabilidade:** 
- Definir exceções de negócio
- Tratamento global de erros
- Padronização de respostas de erro

---

### 7. **repository/** - Repositórios JPA
Camada de acesso a dados.

```
repository/
├── AuditLogRepository.java                            # Repositório de logs de auditoria
├── DocumentoRepository.java                           # Repositório de documentos
├── EmpresaRepository.java                             # Repositório de empresas
└── UsuarioRepository.java                             # Repositório de usuários
```

**Responsabilidade:** 
- Interface com banco de dados
- Queries customizadas (JPQL, Native SQL)
- Operações CRUD

---

### 8. **service/** - Serviços de Negócio
Lógica de negócio da aplicação.

```
service/
├── AuditLogService.java                               # Serviço de auditoria
├── AuthorizationService.java                          # Serviço de autorização
├── DocumentoService.java                              # Serviço de documentos
├── EmpresaService.java                                # Serviço de empresas
└── UsuarioService.java                                # Serviço de usuários
```

**Responsabilidade:** 
- Implementar regras de negócio
- Orquestrar operações entre repositórios
- Transações
- Validações complexas

---

## 🗄️ Banco de Dados

### Migrations (Flyway)
```
resources/db/migration/
├── V1__criando_estrutura_inicial.sql                  # Estrutura inicial do BD
├── V2__alterando_ids_para_long.sql                    # Alteração de tipos de ID
├── V3__corrigir_auto_increment_ids.sql               # Correção auto-increment
└── V4__create_audit_logs_table.sql                   # Tabela de logs de auditoria
```

**Estratégia:** Versionamento incremental com Flyway para controle de schema.

---

## 🎯 Padrões Arquiteturais Utilizados

### 1. **Arquitetura em Camadas (Layered Architecture)**
```
Controller → Service → Repository → Database
     ↓          ↓
    DTOs     Domain
```

### 2. **Separação de Responsabilidades**
- **Controllers:** Apenas roteamento e validação básica
- **Services:** Lógica de negócio
- **Repositories:** Acesso a dados
- **DTOs:** Contratos de API

### 3. **Inversão de Dependência**
- Uso de interfaces para repositórios
- Injeção de dependência via Spring

### 4. **Auditoria**
- Hibernate Envers para rastreamento de mudanças
- Anotação customizada `@Auditable`
- Logs de auditoria separados

---

## 🔧 Tecnologias Principais

| Camada | Tecnologia |
|--------|------------|
| Framework | Spring Boot 3.5.10 |
| Linguagem | Java 21 |
| Persistência | JPA/Hibernate + Envers |
| Banco de Dados | MySQL (via docker-compose) |
| Migração de BD | Flyway |
| Armazenamento | MinIO |
| Build Tool | Maven |
| Segurança | Spring Security |
| AOP | Spring AOP |

---

## 📝 Convenções de Nomenclatura

### Packages
- **Singular:** `domain`, `controller`, `service`, `repository`
- **Descritivo:** Nomes claros indicando responsabilidade

### Classes
- **Controllers:** `*Controller.java`
- **Services:** `*Service.java`
- **Repositories:** `*Repository.java`
- **DTOs Request:** `*Request.java` ou `*DTO.java`
- **DTOs Response:** `*Response.java` ou `*DTO.java`
- **Entidades:** Nome da entidade em português

### Métodos
- CRUD padrão: `save`, `findById`, `findAll`, `delete`, `update`
- Queries customizadas: `findBy*`, `existsBy*`, `countBy*`

---

## 🚀 Fluxo de Requisição

```
1. Cliente HTTP
   ↓
2. Controller (validação inicial, conversão DTO)
   ↓
3. Service (regras de negócio, transações)
   ↓
4. Repository (queries, persistência)
   ↓
5. Banco de Dados
   ↓
6. Repository (retorno de entidades)
   ↓
7. Service (processamento, conversão)
   ↓
8. Controller (conversão para DTO Response)
   ↓
9. Cliente HTTP
```

---

## 🧪 Testes

### Estrutura de Testes
```
src/test/java/br/com/vendas/passagem/omnibus/
└── controller/
    ├── EmpresaControllerTest.java                     # Testes unitários do EmpresaController
    └── UsuarioControllerTest.java                     # Testes unitários do UsuarioController
```

### Configuração de Testes
- **Framework:** JUnit 5 (Jupiter)
- **Mocking:** Mockito com anotação `@MockitoBean`
- **Test Context:** `@WebMvcTest` para testes de controller em camada isolada
- **Security Testing:** `@WithMockUser` para simulação de usuários autenticados

### Testes Implementados

#### **EmpresaControllerTest.java**
- ✅ `deveCriarEmpresaComSucessoComoAdmin` - Criação de empresa por administrador
- ✅ `deveCriarEmpresaComSucessoComoPassageiro` - Criação de empresa por passageiro
- ✅ `deveRetornar400QuandoCriarComDadosInvalidos` - Validação de dados obrigatórios
- ✅ `deveRetornar400QuandoCriarComCNPJInvalido` - Validação de formato CNPJ
- ✅ `deveBuscarEmpresaPorIdComoAdmin` - Busca de empresa existente
- ✅ `deveAtualizarEmpresaComSucessoComoAdmin` - Atualização por administrador
- ✅ `deveAtualizarEmpresaComSucessoComoEmpresa` - Atualização por empresa
- ✅ `deveDeletarEmpresaComSucessoComoAdmin` - Deleção por administrador
- ✅ `deveDeletarEmpresaComSucessoComoEmpresa` - Deleção por empresa

#### **UsuarioControllerTest.java**
- ✅ `deveBuscarUsuarioPorIdComoAdmin` - Busca de usuário por administrador
- ✅ `deveAtualizarUsuarioComSucesso` - Atualização de dados do usuário
- ✅ `deveRetornar400QuandoAtualizarComDadosInvalidos` - Validação de dados obrigatórios
- ✅ `deveDeletarUsuarioComSucesso` - Deleção de usuário
- ✅ `deveFazerUploadDeDocumentoComSucesso` - Upload de documento com validações

### Mocks Configurados
- **UsuarioService:** Serviço de usuários
- **DocumentoService:** Serviço de documentos
- **EmpresaService:** Serviço de empresas
- **TokenService:** Serviço de geração/validação de tokens
- **UsuarioRepository:** Repositório de usuários

### Cobertura de Testes
- **Total de testes:** 14 (9 para Empresa + 5 para Usuário)
- **Status:** ✅ Todos os testes passando
- **Taxa de cobertura:** Controller layer

### Nota sobre Testes de Autorização
Testes de autorização (401/403) foram comentados pois `@AutoConfigureMockMvc(addFilters = false)` desabilita filtros de segurança. Para testar autorização completa, é necessário:
1. Usar `@SpringBootTest` para teste de integração
2. Remover `addFilters = false`
3. Configurar usuários autenticados adequadamente

---

## 📝 Última Atualização

1. Documentar DTOs específicos (Request/Response)
2. Criar diagrama ER do banco de dados
3. Documentar endpoints da API (Swagger/OpenAPI)
4. Adicionar testes de integração
5. Documentar regras de negócio específicas

---

## � Melhorias Recentes (Fevereiro 2026)

### ✅ Migração de Anotações Spring Security
- **Atualizado:** `@MockBean` → `@MockitoBean` (Spring Boot 3.4.0+)
- **Motivo:** `@MockBean` foi depreciado e será removido nas versões futuras
- **Arquivos afetados:** 
  - `UsuarioControllerTest.java`
  - `EmpresaControllerTest.java`

### ✅ Reorganização de Exceções
- **Movido:** `TokenGenerationException` de `config/security/exception/` para `exception/`
- **Motivo:** Centralizar todas as exceções customizadas no mesmo diretório (`exception/`)
- **Benefício:** Estrutura mais clara e organizada, seguindo padrão do projeto

### ✅ Testes Unitários
- **Implementados:** 14 testes unitários para controllers
- **Framework:** JUnit 5 + Mockito com `@MockitoBean`
- **Status:** Todos os testes passando (BUILD SUCCESS)

### ✅ Ajustes no Fixture de Testes
- **Corrigido:** CPF de teste "12345678901" → "12345678909" (CPF válido)
- **Motivo:** Passagem em validação `@CPF` do Hibernate Validator

---

## 📚 Próximos Passos Recomendados

1. Implementar testes de integração completos com `@SpringBootTest`
2. Adicionar testes de autorização (401/403) com contexto real
3. Documentar endpoints da API com Swagger/OpenAPI 3.0
4. Adicionar testes de service layer
5. Implementar testes de repository layer com testcontainers
6. Adicionar métricas de cobertura de testes (JaCoCo)
7. Documentar regras de negócio específicas por serviço
8. Criar diagrama ER do banco de dados (ERDPlus ou similar)

---

## 📄 Documentos Relacionados

- [HELP.md](HELP.md) - Documentação de ajuda
- [EXEMPLOS_TESTES_API.md](EXEMPLOS_TESTES_API.md) - Exemplos de testes da API
- [docker-compose.yml](docker-compose.yml) - Configuração de containers

---

**Última atualização:** Fevereiro de 2026  
**Versão do Projeto:** 0.0.1-SNAPSHOT
