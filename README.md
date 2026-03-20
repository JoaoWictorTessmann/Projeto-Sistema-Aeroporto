<div align="center">

# ✈ AeroSys — Sistema de Gerenciamento Aeroportuário

![Java](https://img.shields.io/badge/Java-25-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.0-brightgreen?style=for-the-badge&logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
![Hibernate](https://img.shields.io/badge/Hibernate-7.1-yellow?style=for-the-badge&logo=hibernate)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.x-darkgreen?style=for-the-badge&logo=thymeleaf)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI_3-green?style=for-the-badge&logo=swagger)

Sistema web completo para gerenciamento de voos, pilotos e companhias aéreas, com interface visual (Thymeleaf) e API REST documentada (Swagger).

</div>

---

## 📋 Índice

- [Visão Geral](#-visão-geral)
- [Tecnologias](#-tecnologias)
- [Requisitos](#-requisitos)
- [Instalação e Configuração](#-instalação-e-configuração)
- [Como Rodar](#-como-rodar)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Telas do Sistema](#-telas-do-sistema)
- [API REST](#-api-rest)
  - [Companhias Aéreas](#companhias-aéreas)
  - [Pilotos](#pilotos)
  - [Voos](#voos)
- [Regras de Negócio](#-regras-de-negócio)
- [Banco de Dados](#-banco-de-dados)
- [Testes](#-testes)
- [Erros e Tratamento de Exceções](#-erros-e-tratamento-de-exceções)

---

## 🌐 Visão Geral

O **AeroSys** é uma aplicação Spring Boot que centraliza o controle operacional de um aeroporto, permitindo:

- Cadastrar, consultar, editar e remover **companhias aéreas**
- Cadastrar, consultar, editar e remover **pilotos** com validação de CPF e geração automática de matrícula
- Criar e gerenciar **voos** com controle de status (AGENDADO → EM_VOO → CONCLUIDO / CANCELADO)
- Acessar tudo via **interface web** (Thymeleaf) ou diretamente pela **API REST** documentada no Swagger

---

## 🛠 Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 25 | Linguagem principal |
| Spring Boot | 4.0.0 | Framework base |
| Spring Data JPA | — | Persistência de dados |
| Spring Web MVC | — | Controllers REST e Thymeleaf |
| Hibernate | 7.1.8 | ORM |
| MySQL | 8.0 | Banco de dados principal |
| H2 | 2.1.214 | Banco em memória para testes |
| Thymeleaf | 3.x | Interface web |
| SpringDoc OpenAPI | 2.8.6 | Documentação Swagger |
| JUnit 5 | — | Testes unitários e integração |
| Mockito | — | Mocks em testes unitários |

---

## ✅ Requisitos

Antes de rodar o projeto, certifique-se de ter instalado:

- [Java 25+](https://adoptium.net/)
- [Maven 3.8+](https://maven.apache.org/)
- [MySQL 8.0+](https://dev.mysql.com/downloads/mysql/) com MySQL Workbench
- IDE recomendada: [IntelliJ IDEA](https://www.jetbrains.com/idea/) ou [VS Code](https://code.visualstudio.com/)

---

## ⚙ Instalação e Configuração

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/aeroporto.git
cd aeroporto
```

### 2. Crie o banco de dados no MySQL

Abra o **MySQL Workbench** e execute:

```sql
CREATE DATABASE sistemaaeroporto;
```

### 3. Configure o `application.properties`

O arquivo está em `src/main/resources/application.properties`. Ajuste as credenciais conforme seu ambiente:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sistemaaeroporto?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=SUA_SENHA
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

> ⚠️ O Hibernate cria as tabelas automaticamente com `ddl-auto=update`. Se preferir criar manualmente, use o script SQL disponível em [`aeroporto_tabelas.sql`](./aeroporto_tabelas.sql).

### 4. Instale as dependências

```bash
mvn clean install
```

---

## ▶ Como Rodar

```bash
mvn spring-boot:run
```

Após subir, acesse:

| Recurso | URL |
|---|---|
| 🏠 Interface Web | http://localhost:8080/ |
| 📋 Lista de Voos | http://localhost:8080/voos |
| 👨‍✈️ Lista de Pilotos | http://localhost:8080/pilotos |
| 🏢 Lista de Companhias | http://localhost:8080/companhias |
| 📖 Swagger UI | http://localhost:8080/docs |
| 📄 OpenAPI JSON | http://localhost:8080/api-docs |

---

## 📁 Estrutura do Projeto

```
src/main/java/sistema/aeroporto/
│
├── AeroportoApplication.java          # Classe principal
│
├── controller/
│   ├── CompanhiaAereaController.java  # API REST /api/companhias
│   ├── PilotoController.java          # API REST /api/pilotos
│   ├── VooController.java             # API REST /api/voos
│   └── view/
│       ├── HomeViewController.java    # GET /
│       ├── CompanhiaAereaViewController.java
│       ├── PilotoViewController.java
│       └── VooViewController.java
│
├── service/
│   ├── CompanhiaAereaService.java
│   ├── PilotoService.java
│   └── VooService.java
│
├── repository/
│   ├── CompanhiaAereaRepository.java
│   ├── PilotoRepository.java
│   └── VooRepository.java
│
├── model/
│   ├── CompanhiaAerea.java
│   ├── Piloto.java
│   ├── Voo.java
│   └── enums/
│       ├── CompanhiaAereaStatus.java  # ATIVA, INATIVA
│       ├── PilotoStatus.java          # ATIVO, INATIVO
│       └── VooStatus.java             # AGENDADO, EM_VOO, CONCLUIDO, CANCELADO
│
├── dto/
│   ├── request/
│   │   ├── CompanhiaAereaRequest.java
│   │   ├── CompanhiaAereaUpdateRequest.java
│   │   ├── PilotoRequest.java
│   │   ├── PilotoUpdateRequest.java
│   │   ├── VooRequest.java
│   │   └── VooUpdateRequest.java
│   └── response/
│       ├── CompanhiaAereaResponse.java
│       ├── PilotoResponse.java
│       └── VooResponse.java
│
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── NotFoundCompanhiaAereaException.java
│   ├── NotFoundPilotoException.java
│   ├── NotFoundVooException.java
│   ├── CnpjInvalidoException.java
│   ├── CnpjJaCadastradoException.java
│   ├── CpfInvalidoException.java
│   ├── CpfJaCadastradoException.java
│   └── ... (demais exceções de negócio)
│
└── util/
    ├── CnpjUtils.java                 # Validação de CNPJ
    └── CpfUtils.java                  # Validação e limpeza de CPF

src/main/resources/
├── application.properties             # Config MySQL (produção)
├── application-test.properties        # Config H2 (testes)
├── static/css/
│   └── global.css                     # Estilos globais
└── templates/
    ├── fragments/navbar.html          # Navbar compartilhada
    ├── index.html                     # Tela inicial
    ├── voo/
    │   ├── lista.html
    │   ├── detalhe.html
    │   └── formulario.html
    ├── piloto/
    │   ├── lista.html
    │   ├── detalhe.html
    │   └── formulario.html
    └── companhia/
        ├── lista.html
        ├── detalhe.html
        └── formulario.html
```

---

## 🖥 Telas do Sistema

### Home — `GET /`
Tela de boas-vindas com banner do sistema, acesso rápido às três entidades e tabela com os 5 voos mais recentes.

### Voos — `GET /voos`
Listagem completa com filtros por status (AGENDADO, EM_VOO, CONCLUIDO, CANCELADO), link para detalhe e botão de novo voo.

### Detalhe do Voo — `GET /voos/{id}`
Exibe todas as informações do voo, incluindo dados do piloto e da companhia vinculados.

### Formulário de Voo — `GET /voos/novo` e `GET /voos/editar/{id}`
Formulário com selects de piloto e companhia populados dinamicamente. O submit chama a API REST via `fetch` e redireciona ao detalhe.

> As telas de **Piloto** e **Companhia** seguem o mesmo padrão: lista, detalhe e formulário.

---

## 📡 API REST

Base URL: `http://localhost:8080/api`

A documentação interativa completa está disponível em: **http://localhost:8080/docs**

---

### Companhias Aéreas

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/companhias` | Cadastrar nova companhia |
| `GET` | `/api/companhias` | Listar todas as companhias |
| `GET` | `/api/companhias/{id}` | Buscar por ID |
| `GET` | `/api/companhias/nome/{nome}` | Buscar por nome |
| `GET` | `/api/companhias/cnpj/{cnpj}` | Buscar por CNPJ |
| `PUT` | `/api/companhias/{id}` | Atualizar companhia |
| `DELETE` | `/api/companhias/{id}` | Remover companhia |

#### Exemplo — Cadastrar Companhia

**Request:**
```json
POST /api/companhias
{
  "nome": "Azul Linhas Aéreas",
  "cnpj": "09.296.295/0001-60",
  "dataFundacao": "2008-05-15",
  "seguroAeronave": true,
  "status": "ATIVA"
}
```

**Response `201`:**
```json
{
  "id": 1,
  "nome": "Azul Linhas Aéreas",
  "cnpj": "09296295000160",
  "dataFundacao": "2008-05-15",
  "seguroAeronave": true,
  "status": "ATIVA"
}
```

#### Exemplo — Atualizar Companhia

**Request:**
```json
PUT /api/companhias/1
{
  "nome": "Azul Linhas Aéreas",
  "seguroAeronave": false,
  "status": "INATIVA"
}
```

---

### Pilotos

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/pilotos` | Cadastrar novo piloto |
| `GET` | `/api/pilotos` | Listar todos os pilotos |
| `GET` | `/api/pilotos/{id}` | Buscar por ID |
| `GET` | `/api/pilotos/cpf/{cpf}` | Buscar por CPF |
| `GET` | `/api/pilotos/matricula/{matricula}` | Buscar por matrícula |
| `PUT` | `/api/pilotos/{id}` | Atualizar piloto |
| `DELETE` | `/api/pilotos/{id}` | Remover piloto |

#### Exemplo — Cadastrar Piloto

**Request:**
```json
POST /api/pilotos
{
  "nome": "Carlos Eduardo Souza",
  "idade": 42,
  "genero": "M",
  "cpf": "111.444.777-35",
  "habilitacao": "ATPL-A",
  "status": "ATIVO"
}
```

**Response `201`:**
```json
{
  "id": 1,
  "nome": "Carlos Eduardo Souza",
  "idade": 42,
  "genero": "M",
  "cpf": "11144477735",
  "dataRenovacao": "2026-03-19",
  "matricula": "PIL20260001",
  "habilitacao": "ATPL-A",
  "status": "ATIVO"
}
```

> 💡 A matrícula é gerada automaticamente no formato `PIL{ano}{id:04d}`. Não é necessário enviá-la no cadastro.

---

### Voos

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/voos` | Criar novo voo |
| `GET` | `/api/voos` | Listar todos os voos |
| `GET` | `/api/voos/{id}` | Buscar por ID |
| `GET` | `/api/voos/status/{status}` | Filtrar por status |
| `GET` | `/api/voos/piloto/{pilotoId}` | Voos de um piloto |
| `GET` | `/api/voos/companhia/{companhiaId}` | Voos de uma companhia |
| `PUT` | `/api/voos/{vooId}` | Atualizar dados do voo |
| `POST` | `/api/voos/iniciar/{vooId}` | Iniciar voo |
| `PATCH` | `/api/voos/{id}/finalizar` | Finalizar voo |
| `PATCH` | `/api/voos/cancelar/{vooId}?motivoCancelamento=...` | Cancelar voo |

#### Exemplo — Criar Voo

**Request:**
```json
POST /api/voos
{
  "pilotoId": 1,
  "companhiaId": 1,
  "codigo": "AD1234",
  "origem": "SBGR",
  "destino": "SBBR",
  "horarioPartidaPrevisto": "2026-04-01T08:00:00",
  "horarioChegadaPrevisto": "2026-04-01T09:30:00"
}
```

**Response `201`:**
```json
{
  "id": 1,
  "piloto": { "id": 1, "nome": "Carlos Eduardo Souza", ... },
  "companhia": { "id": 1, "nome": "Azul Linhas Aéreas", ... },
  "codigo": "AD1234",
  "origem": "SBGR",
  "destino": "SBBR",
  "horarioPartidaPrevisto": "2026-04-01T08:00:00",
  "horarioChegadaPrevisto": "2026-04-01T09:30:00",
  "horarioPartidaReal": null,
  "horarioChegadaReal": null,
  "motivoCancelamento": "",
  "status": "AGENDADO"
}
```

#### Exemplo — Cancelar Voo

```
PATCH /api/voos/cancelar/1?motivoCancelamento=Condições climáticas adversas
```

#### Fluxo de Status do Voo

```
AGENDADO ──► EM_VOO ──► CONCLUIDO
    │
    └──────────────────► CANCELADO
```

---

## 📐 Regras de Negócio

### Companhia Aérea
- CNPJ deve ser válido (validação por dígitos verificadores)
- CNPJ e nome devem ser únicos no sistema
- Apenas companhias com status **ATIVA** podem ter voos criados
- Status aceitos: `ATIVA`, `INATIVA`

### Piloto
- CPF deve ser válido (validação por dígitos verificadores)
- CPF deve ser único no sistema
- Piloto deve ter no mínimo **18 anos**
- Matrícula gerada automaticamente: `PIL{ano}{id:04d}` (ex: `PIL20260001`)
- CPF e matrícula **não podem ser alterados** após o cadastro
- Piloto com status **INATIVO** não pode iniciar voos
- Status aceitos: `ATIVO`, `INATIVO`

### Voo
- Origem e destino **não podem ser iguais**
- Código do voo deve ser **único**
- Horário de partida deve ser **no futuro**
- Um piloto **não pode ter dois voos no mesmo horário**
- Voo só pode ser **iniciado** se estiver com status `AGENDADO` e o piloto estiver `ATIVO`
- Voo só pode ser **finalizado** se estiver com status `EM_VOO`
- **Motivo de cancelamento é obrigatório** ao cancelar
- Se `horarioChegadaPrevisto` não for informado, assume `horarioPartidaPrevisto + 4 horas`
- Status aceitos: `AGENDADO`, `EM_VOO`, `CONCLUIDO`, `CANCELADO`

---

## 🗄 Banco de Dados

### Diagrama de Relacionamento

```
┌──────────────────┐       ┌──────────────────┐
│  companhia_aerea │       │      piloto       │
├──────────────────┤       ├──────────────────┤
│ id (PK)          │       │ id (PK)          │
│ nome (UNIQUE)    │       │ nome             │
│ cnpj (UNIQUE)    │       │ cpf (UNIQUE)     │
│ data_fundacao    │       │ matricula (UNIQUE)│
│ seguro_aeronave  │       │ idade            │
│ status           │       │ genero           │
└────────┬─────────┘       │ habilitacao      │
         │                 │ data_renovacao   │
         │ 1:N             │ status           │
         │                 └───────┬──────────┘
         │                         │ 1:N
         ▼                         ▼
┌──────────────────────────────────────────────┐
│                     voo                      │
├──────────────────────────────────────────────┤
│ id (PK)                                      │
│ companhia_id (FK → companhia_aerea.id)       │
│ piloto_id (FK → piloto.id)                   │
│ codigo (UNIQUE)                              │
│ origem (CHAR 4 — código ICAO)                │
│ destino (CHAR 4 — código ICAO)               │
│ horario_partida_previsto                     │
│ horario_chegada_previsto                     │
│ horario_partida_real (nullable)              │
│ horario_chegada_real (nullable)              │
│ motivo_cancelamento                          │
│ status                                       │
└──────────────────────────────────────────────┘
```

### Scripts SQL

Os scripts para criação do banco e inserção de dados de teste estão disponíveis na raiz do projeto:

- [`aeroporto_tabelas.sql`](./aeroporto_tabelas.sql) — Criação das tabelas e constraints
- [`aeroporto_inserts.sql`](./aeroporto_inserts.sql) — 20 registros de teste por entidade

Para rodar:
```bash
mysql -u root -p sistemaaeroporto < aeroporto_tabelas.sql
mysql -u root -p sistemaaeroporto < aeroporto_inserts.sql
```

---

## 🧪 Testes

O projeto possui testes **unitários** (Mockito) e de **integração** (SpringBootTest + H2) para os três services.

### Rodar todos os testes

```bash
mvn test
```

### Cobertura de testes

| Classe | Tipo | Cenários |
|---|---|---|
| `CompanhiaAereaServiceTest` | Unitário (Mockito) | Listar, buscar por nome/CNPJ, salvar, atualizar, deletar, erros |
| `CompanhiaAereaServiceIntegrationTest` | Integração (H2) | Fluxo completo com banco em memória |
| `PilotoServiceTeste` | Unitário (Mockito) | Listar, buscar por CPF, salvar com matrícula, atualizar, deletar |
| `PilotoServiceIntegrationTest` | Integração (H2) | Fluxo completo com banco em memória |
| `VooServiceTeste` | Unitário (Mockito) | Criar, iniciar, cancelar, finalizar, filtros, erros |
| `VooServiceIntegrationTest` | Integração (H2) | Fluxo completo com validações de negócio |

### Configuração para testes

Os testes usam banco H2 em memória. As configurações estão em `src/test/resources/application.properties`:

```properties
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=update
```

---

## ⚠ Erros e Tratamento de Exceções

Todos os erros são tratados pelo `GlobalExceptionHandler` e retornam respostas padronizadas:

```json
{
  "status": 404,
  "message": "Piloto não encontrado"
}
```

### Tabela de Erros

| Código | Situação |
|---|---|
| `404` | Entidade não encontrada (piloto, companhia, voo) |
| `409` | Regra de negócio violada (CNPJ duplicado, CPF inválido, origem = destino, etc.) |
| `500` | Erro interno não tratado |

### Exceções de Negócio

| Exceção | Mensagem |
|---|---|
| `CnpjInvalidoException` | CNPJ inválido |
| `CnpjJaCadastradoException` | CNPJ já cadastrado |
| `CpfInvalidoException` | CPF inválido |
| `CpfJaCadastradoException` | CPF já cadastrado |
| `MenorIdadeException` | Piloto deve ter ao menos 18 anos |
| `NomeJaCadastradoException` | Nome já cadastrado |
| `OrigemDestinoIguaisException` | Origem e destino não podem ser iguais |
| `HorarioPartidaPassadoException` | Horário de partida não pode ser no passado |
| `PilotoOutroVooException` | Piloto já está escalado para outro voo nesse horário |
| `CompanhiaNaoAtivaException` | Companhia não está ativa |
| `PilotoInativoException` | Piloto não pode iniciar o voo |
| `SomenteAgendadoException` | Somente voos agendados podem ser iniciados |
| `SomenteEmVooException` | Somente voos em andamento podem ser finalizados |
| `MotivoCancelamentoObrigatorioException` | Motivo do cancelamento é obrigatório |
| `CodigoVooExistenteException` | Código de voo já existente |

---

<div align="center">

Feito com ☕ e Spring Boot

</div>