<div align="center">

# ✈ AeroSys — Sistema de Gerenciamento Aeroportuário

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.0.0-brightgreen?style=for-the-badge&logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql)
![H2](https://img.shields.io/badge/H2-2.1.214-lightgrey?style=for-the-badge)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.x-darkgreen?style=for-the-badge&logo=thymeleaf)
![Swagger](https://img.shields.io/badge/SpringDoc_OpenAPI-2.8.6-green?style=for-the-badge&logo=swagger)

Sistema web completo para gerenciamento de voos, pilotos e companhias aéreas, com interface visual (Thymeleaf) e API REST documentada (Swagger/OpenAPI).

</div>

---

## 📋 Índice

- [Visão Geral](#-visão-geral)
- [Motivação](#-motivação)
- [Demo](#-demo)
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

- Cadastrar, consultar, editar e remover **companhias aéreas** com validação de CNPJ
- Cadastrar, consultar, editar e remover **pilotos** com validação de CPF e geração automática de matrícula
- Criar e gerenciar **voos** com controle de status (`AGENDADO` → `VOANDO` → `CONCLUIDO` / `CANCELADO`)
- Acessar tudo via **interface web** (Thymeleaf) ou diretamente pela **API REST** documentada no Swagger

---

## 💡 Motivação

O **Aeroporto Regional de Santa Vitória do Palmar**, localizado no extremo sul do Rio Grande do Sul, operava com planilhas Excel espalhadas entre departamentos, cadernos físicos na torre de controle e comunicação feita por telefone e rádio.

Com a expansão do aeroporto em março de 2025 — dobrando sua capacidade —, o que antes era desorganizado virou um colapso operacional. Voos eram remarcados sem avisar passageiros, tripulações ficavam sem escala por falha de comunicação, bagagens eram enviadas para voos errados e, no episódio mais crítico, **dois voos foram escalados para a mesma pista ao mesmo tempo** — evitado apenas pela atenção de um controlador no último momento.

A situação levou a **ANAC a abrir uma investigação**, ameaçando suspender as licenças de operação do aeroporto.

Em reunião de emergência, o diretor de operações **Eng. Carlos Medeiros** determinou a criação de um sistema centralizado que integrasse voos, tripulação e operações em tempo real. A equipe de TI interna recebeu a missão de construir o **AeroSys do zero**, com Java + Spring Boot + MySQL — em **60 dias**, antes da próxima auditoria da ANAC.

---

## 🌍 Demo

> ✅ Sistema disponível em produção!

**[https://aerosys.onrender.com/](https://aerosys.onrender.com/)**

---

## 🛠 Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 17 | Linguagem principal |
| Spring Boot | 4.0.0 | Framework base |
| Spring Data JPA | gerenciado pelo Boot | Persistência de dados |
| Spring Web MVC | gerenciado pelo Boot | Controllers REST e Thymeleaf |
| Spring Validation | gerenciado pelo Boot | Validação de DTOs via anotações Jakarta |
| Hibernate | gerenciado pelo Boot | ORM |
| MySQL Connector/J | gerenciado pelo Boot | Driver MySQL para produção |
| H2 | 2.1.214 | Banco em memória para testes |
| Thymeleaf | gerenciado pelo Boot | Interface web (templates HTML) |
| Thymeleaf Layout Dialect | gerenciado pelo Boot | Suporte a layouts no Thymeleaf |
| SpringDoc OpenAPI | 2.8.6 | Documentação Swagger UI |
| Spring Boot DevTools | gerenciado pelo Boot | Reload automático em desenvolvimento |
| Spring Boot Test | gerenciado pelo Boot | JUnit 5 + Mockito para testes |

---

## ✅ Requisitos

Antes de rodar o projeto, certifique-se de ter instalado:

- [Java 17+](https://adoptium.net/)
- [Maven 3.8+](https://maven.apache.org/)
- [MySQL 8.0+](https://dev.mysql.com/downloads/mysql/)
- IDE recomendada: [IntelliJ IDEA](https://www.jetbrains.com/idea/) ou [VS Code](https://code.visualstudio.com/)

---

## ⚙ Instalação e Configuração

### 1. Clone o repositório

```bash
git clone https://github.com/seu-usuario/aeroporto.git
cd aeroporto
```

### 2. Crie o banco de dados no MySQL

```sql
CREATE DATABASE sistemaaeroporto;
```

> Esse é o único passo manual necessário. Todo o resto é automático. ✅

### 3. Configure o `application.properties`

O arquivo está em `src/main/resources/application.properties`. Ajuste apenas as credenciais:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/sistemaaeroporto?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=SUA_SENHA
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

springdoc.swagger-ui.path=/docs
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.operationsSorter=alpha
springdoc.swagger-ui.tagsSorter=alpha
```

### 4. Dados de exemplo — DataSeeder

O projeto possui um `DataSeeder` que popula o banco automaticamente na **primeira inicialização**. Ele verifica se já existem dados antes de inserir, garantindo que nunca haverá duplicatas em restarts.

Na primeira vez que a aplicação subir, o console exibirá:

```
>>> Banco vazio. Iniciando seed...
>>> 20 companhias inseridas.
>>> 20 pilotos inseridos.
>>> 20 voos inseridos.
>>> Seed concluído com sucesso.
```

Nos restarts seguintes:

```
>>> Banco já possui dados. Seed ignorado.
```

### 5. Instale as dependências

```bash
mvn clean install
```

---

## ▶ Como Rodar

```bash
mvn spring-boot:run
```

Ao subir, o console exibirá:

```
Aeroporto Application started successfully.
Access application at: http://localhost:8080/
```

Recursos disponíveis:

| Recurso | URL |
|---|---|
| 🏠 Interface Web | http://localhost:8080/ |
| ✈ Voos | http://localhost:8080/voos |
| 👨‍✈️ Pilotos | http://localhost:8080/pilotos |
| 🏢 Companhias | http://localhost:8080/companhias |
| 📖 Swagger UI | http://localhost:8080/docs |
| 📄 OpenAPI JSON | http://localhost:8080/api-docs |

---

## 📁 Estrutura do Projeto

```
src/main/java/sistema/aeroporto/
│
├── AeroportoApplication.java
├── DataSeeder.java                    # Seed automático na primeira inicialização
│
├── controller/
│   ├── CompanhiaAereaController.java      # API REST /api/companhias
│   ├── PilotoController.java              # API REST /api/pilotos
│   ├── VooController.java                 # API REST /api/voos
│   └── view/
│       ├── HomeViewController.java        # GET /
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
│       ├── CompanhiaAereaStatus.java      # ATIVA, INATIVA
│       ├── PilotoStatus.java              # ATIVO, INATIVO
│       └── VooStatus.java                 # AGENDADO, VOANDO, CONCLUIDO, CANCELADO
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
│   ├── CnpjInvalidoException.java
│   ├── CnpjJaCadastradoException.java
│   ├── CodigoVooExistenteException.java
│   ├── CodigoVooObrigatorioException.java
│   ├── CompanhiaNaoAtivaException.java
│   ├── CpfInvalidoException.java
│   ├── CpfJaCadastradoException.java
│   ├── CpfObrigatorioException.java
│   ├── HorarioPartidaObrigatorioException.java
│   ├── HorarioPartidaPassadoException.java
│   ├── MenorIdadeException.java
│   ├── MotivoCancelamentoObrigatorioException.java
│   ├── NomeJaCadastradoException.java
│   ├── NomeObrigatorioException.java
│   ├── NotFoundCompanhiaAereaException.java
│   ├── NotFoundPilotoException.java
│   ├── NotFoundVooException.java
│   ├── OrigemDestinoIguaisException.java
│   ├── OrigemDestinoObrigatorioException.java
│   ├── PilotoInativoException.java
│   ├── PilotoObrigatorioException.java
│   ├── PilotoOutroVooException.java
│   ├── SemPilotoException.java
│   ├── SomenteAgendadoException.java
│   └── SomenteEmVooException.java
│
└── util/
    ├── CnpjUtils.java                     # Validação e formatação de CNPJ
    └── CpfUtils.java                      # Validação, limpeza e formatação de CPF

src/main/resources/
├── application.properties                 # Config MySQL (produção)
├── schema.sql                             # Referência DDL das tabelas (execução manual)
├── static/
│   └── css/
│       ├── global.css                     # Estilos globais
│       └── main.css                       # Estilos da home
└── templates/
    ├── fragments/
    │   └── navbar.html                    # Navbar compartilhada
    ├── index.html                         # Tela inicial
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

src/test/java/sistema/aeroporto/
├── AeroportoApplicationTests.java
└── service/
    ├── CompanhiaAereaServiceTest.java          # Unitário (Mockito)
    ├── CompanhiaAereaServiceIntegrationTest.java  # Integração (H2)
    ├── PilotoServiceTeste.java                 # Unitário (Mockito)
    ├── PilotoServiceIntegrationTest.java       # Integração (H2)
    ├── VooServiceTeste.java                    # Unitário (Mockito)
    └── VooServiceIntegrationTest.java          # Integração (H2)

src/test/resources/
└── application.properties                     # Config H2 (testes)
```

---

## 🖥 Telas do Sistema

### Home — `GET /`
Tela de boas-vindas com hero banner, acesso rápido às três entidades e tabela com os voos mais recentes (limitada aos 5 primeiros).

### Voos — `GET /voos`
Listagem completa com filtros por status (`AGENDADO`, `VOANDO`, `CONCLUIDO`, `CANCELADO`). Os filtros navegam para `/voos/status/{status}`.

### Detalhe do Voo — `GET /voos/{id}`
Exibe todas as informações do voo com os dados do piloto e da companhia vinculados em cards separados.

### Formulário de Voo — `GET /voos/novo` e `GET /voos/editar/{id}`
Formulário com selects de piloto e companhia populados pelo servidor via Thymeleaf. O submit chama a API REST via `fetch` e redireciona ao detalhe do voo.

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
| `GET` | `/api/companhias/nome/{nome}` | Buscar por nome exato |
| `GET` | `/api/companhias/cnpj/{cnpj}` | Buscar por CNPJ (com ou sem formatação) |
| `PUT` | `/api/companhias/{id}` | Atualizar nome, seguro e status |
| `DELETE` | `/api/companhias/{id}` | Remover companhia |

#### Exemplo — Cadastrar Companhia

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
  "cnpj": "09.296.295/0001-60",
  "dataFundacao": "2008-05-15",
  "seguroAeronave": true,
  "status": "ATIVA"
}
```

#### Exemplo — Atualizar Companhia

> ⚠️ O CNPJ **não pode ser alterado** via update. Apenas `nome`, `seguroAeronave` e `status` são aceitos.

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
| `PUT` | `/api/pilotos/{id}` | Atualizar nome, idade, gênero e status |
| `DELETE` | `/api/pilotos/{id}` | Remover piloto |

#### Exemplo — Cadastrar Piloto

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
  "cpf": "111.444.777-35",
  "dataRenovacao": "2026-03-21",
  "matricula": "PIL20260001",
  "habilitacao": "ATPL-A",
  "status": "ATIVO"
}
```

> 💡 A matrícula é gerada automaticamente no formato `PIL{ano}{id:04d}` (ex: `PIL20260001`). Não é necessário enviá-la no cadastro.
> A `dataRenovacao` é definida automaticamente como a data do cadastro.

> ⚠️ CPF e matrícula **não podem ser alterados** após o cadastro.

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
| `PUT` | `/api/voos/{vooId}` | Atualizar horários reais e status |
| `POST` | `/api/voos/iniciar/{vooId}` | Iniciar voo (AGENDADO → VOANDO) |
| `PATCH` | `/api/voos/{id}/finalizar` | Finalizar voo (VOANDO → CONCLUIDO) |
| `PATCH` | `/api/voos/cancelar/{vooId}?motivoCancelamento=...` | Cancelar voo |

#### Exemplo — Criar Voo

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
  "piloto": { "id": 1, "nome": "Carlos Eduardo Souza", "matricula": "PIL20260001", ... },
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

> 💡 Se `horarioChegadaPrevisto` não for informado, o sistema assume `horarioPartidaPrevisto + 4 horas`.

#### Exemplo — Cancelar Voo

```
PATCH /api/voos/cancelar/1?motivoCancelamento=Condições climáticas adversas
```

#### Fluxo de Status do Voo

```
AGENDADO ──► VOANDO ──► CONCLUIDO
    │
    └──────────────────► CANCELADO
```

> ⚠️ O status `VOANDO` é o nome real do enum no código. A documentação anterior usava `EM_VOO`, que foi corrigido.

---

## 📐 Regras de Negócio

### Companhia Aérea
- CNPJ deve ser válido (algoritmo de dígitos verificadores via `CnpjUtils`)
- CNPJ é normalizado (remove formatação) antes de ser salvo
- CNPJ e nome devem ser únicos no sistema
- Apenas companhias com status `ATIVA` podem ter voos criados
- Status aceitos: `ATIVA`, `INATIVA`

### Piloto
- CPF deve ser válido (algoritmo de dígitos verificadores via `CpfUtils`)
- CPF é limpo (remove pontos e traço) antes de ser salvo
- CPF deve ser único no sistema
- Piloto deve ter no mínimo **18 anos**
- Matrícula gerada automaticamente: `PIL{ano}{id:04d}` (ex: `PIL20260001`)
- `dataRenovacao` é definida automaticamente como a data do cadastro
- CPF e matrícula **não podem ser alterados** após o cadastro
- Piloto com status `INATIVO` não pode iniciar voos
- Status aceitos: `ATIVO`, `INATIVO`

### Voo
- Origem e destino são obrigatórios e **não podem ser iguais**
- Código do voo é obrigatório e deve ser **único**
- Horário de partida deve ser **no futuro**
- Um piloto **não pode ter dois voos no mesmo horário de partida previsto**
- Voo só pode ser **iniciado** (`AGENDADO → VOANDO`) se o piloto estiver `ATIVO`
- Voo só pode ser **finalizado** (`VOANDO → CONCLUIDO`) se estiver com status `VOANDO`
- **Motivo de cancelamento é obrigatório** ao cancelar
- Se `horarioChegadaPrevisto` não for informado, assume `horarioPartidaPrevisto + 4 horas`
- Status aceitos: `AGENDADO`, `VOANDO`, `CONCLUIDO`, `CANCELADO`

---

## 🗄 Banco de Dados

### Diagrama de Relacionamento

```
┌──────────────────┐       ┌──────────────────┐
│  companhia_aerea │       │      piloto       │
├──────────────────┤       ├──────────────────┤
│ id (PK)          │       │ id (PK)          │
│ nome             │       │ nome             │
│ cnpj             │       │ cpf              │
│ data_fundacao    │       │ matricula        │
│ seguro_aeronave  │       │ idade            │
│ status           │       │ genero           │
└────────┬─────────┘       │ habilitacao      │
         │ 1:N             │ data_renovacao   │
         │                 │ status           │
         │                 └───────┬──────────┘
         │                         │ 1:N
         ▼                         ▼
┌──────────────────────────────────────────────┐
│                     voo                      │
├──────────────────────────────────────────────┤
│ id (PK)                                      │
│ companhia_id (FK → companhia_aerea.id)       │
│ piloto_id (FK → piloto.id)                   │
│ codigo                                       │
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

> As tabelas são criadas automaticamente pelo Hibernate (`ddl-auto=update`).

---

## 🧪 Testes

O projeto possui testes **unitários** com Mockito e testes de **integração** com `@SpringBootTest` + banco H2 em memória, cobrindo os três services.

### Rodar todos os testes

```bash
mvn test
```

### Configuração do ambiente de testes

Os testes usam banco H2 em memória, configurado em `src/test/resources/application.properties`:

```properties
spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
```

### Cobertura de testes

| Classe de Teste | Tipo | Cenários cobertos |
|---|---|---|
| `CompanhiaAereaServiceTest` | Unitário (Mockito) | Listar, buscar por nome/CNPJ, salvar com CNPJ válido/inválido/duplicado, atualizar, deletar |
| `CompanhiaAereaServiceIntegrationTest` | Integração (H2) | Fluxo completo com banco em memória |
| `PilotoServiceTeste` | Unitário (Mockito) | Listar, buscar por CPF, salvar com geração de matrícula, atualizar, deletar |
| `PilotoServiceIntegrationTest` | Integração (H2) | Fluxo completo com banco em memória |
| `VooServiceTeste` | Unitário (Mockito) | Criar, iniciar, cancelar, finalizar, filtros por status/piloto/companhia, todos os erros de negócio |
| `VooServiceIntegrationTest` | Integração (H2) | Fluxo completo com todas as validações de negócio |

---

## ⚠ Erros e Tratamento de Exceções

Todos os erros são tratados pelo `GlobalExceptionHandler` (`@RestControllerAdvice`) e retornam respostas padronizadas:

```json
{
  "status": 404,
  "message": "Piloto não encontrado"
}
```

### Códigos de resposta

| Código | Situação |
|---|---|
| `201` | Recurso criado com sucesso |
| `204` | Recurso removido com sucesso |
| `400` | Dados inválidos (Bean Validation) |
| `404` | Entidade não encontrada |
| `409` | Regra de negócio violada |
| `500` | Erro interno não tratado |

### Exceções de negócio

| Exceção | Mensagem | Código HTTP |
|---|---|---|
| `NotFoundCompanhiaAereaException` | Companhia não encontrada | 404 |
| `NotFoundPilotoException` | Piloto não encontrado | 404 |
| `NotFoundVooException` | Voo não encontrado | 404 |
| `CnpjInvalidoException` | CNPJ inválido | 409 |
| `CnpjJaCadastradoException` | CNPJ já cadastrado | 409 |
| `CpfInvalidoException` | CPF Inválido | 409 |
| `CpfJaCadastradoException` | CPF já cadastrado | 409 |
| `CpfObrigatorioException` | CPF Obrigatório | 409 |
| `CodigoVooExistenteException` | Código de voo já existente | 409 |
| `CodigoVooObrigatorioException` | Código do voo é obrigatório | 409 |
| `CompanhiaNaoAtivaException` | Companhia não está ativa | 409 |
| `MenorIdadeException` | Piloto deve ter no mínimo 18 anos | 409 |
| `NomeJaCadastradoException` | Nome já cadastrado | 409 |
| `NomeObrigatorioException` | Nome Obrigatório | 409 |
| `OrigemDestinoIguaisException` | Origem e destino não podem ser iguais | 409 |
| `OrigemDestinoObrigatorioException` | Origem e destino são obrigatórios | 409 |
| `HorarioPartidaObrigatorioException` | Horário de partida é obrigatório | 409 |
| `HorarioPartidaPassadoException` | Horário de partida não pode ser no passado | 409 |
| `PilotoInativoException` | Piloto não pode iniciar o voo | 409 |
| `PilotoObrigatorioException` | Piloto é obrigatório | 409 |
| `PilotoOutroVooException` | Piloto já está escalado para outro voo nesse horário | 409 |
| `SemPilotoException` | Voo sem piloto | 409 |
| `SomenteAgendadoException` | Somente voos agendados podem ser iniciados | 409 |
| `SomenteEmVooException` | Somente voos iniciados podem ser concluídos | 409 |
| `MotivoCancelamentoObrigatorioException` | Motivo do cancelamento é obrigatório | 409 |

---

<div align="center">

Feito com ☕ e Spring Boot

</div>
