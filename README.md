# Sistema de Aeroporto ✈️

Um sistema de gerenciamento de companhias aéreas, pilotos e voos,
desenvolvido em **Spring Boot**, seguindo boas práticas de arquitetura
(Controller, Service, Repository).

## 📌 Tecnologias Utilizadas

-   Java 17+
-   Spring Boot
-   Spring Web
-   Spring Data JPA
-   Hibernate
-   Banco H2/MySQL (dependendo da sua configuração)
-   Maven

## 📂 Estrutura do Projeto

    src/main/java/sistema/aeroporto
     ├── controller/
     ├── service/
     ├── repository/
     ├── model/
     │     └── enums/
     └── SistemaAeroportoApplication.java

## 🚀 Como Rodar o Projeto

1.  Clone o repositório:

```{=html}
<!-- -->
```
    git clone https://github.com/JoaoWictorTessmann/Projeto-Sistema-Aeroporto.git

2.  Entre na pasta do projeto:

```{=html}
<!-- -->
```
    cd Projeto-Sistema-Aeroporto

3.  Rode o projeto:

```{=html}
<!-- -->
```
    mvn spring-boot:run

4.  Acesse:

```{=html}
<!-- -->
```
    http://localhost:8080

------------------------------------------------------------------------

## 📘 Endpoints Principais

### ✈️ Companhia Aérea

  Método   Rota                      Descrição
  -------- ------------------------- ----------------
  GET      /companhias               Lista todas
  GET      /companhias/nome/{nome}   Busca por nome
  GET      /companhias/cnpj/{cnpj}   Busca por CNPJ
  POST     /companhias               Cria nova
  DELETE   /companhias/{id}          Remove

------------------------------------------------------------------------

## 📄 Licença

Projeto livre para estudos e melhorias.

------------------------------------------------------------------------

## 🤝 Contribuições

Sinta-se à vontade para enviar PRs ou sugerir melhorias!
