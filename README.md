# Biblioteca CloudTech Solutions

Aplicação web em **Java 21 + Spring Boot 3** para gerenciar o acervo de livros de uma biblioteca. Ela permite consultar os livros (todos ou só os disponíveis), consultar um livro pelo ID, cadastrar livros novos e alterar as informações dos livros que já existem.

Desenvolvimento Backend, Aula 11, Exercício 2.

## Tecnologias

- Java 21 e Spring Boot 3.4 (Spring Web e Bean Validation)
- JUnit 5 e MockMvc para os testes
- Maven
- Docker
- GitHub Actions para o pipeline CI/CD
- Thunder Client para os testes manuais

## Endpoints

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/` | Mensagem de boas-vindas |
| GET | `/livros` | Lista os livros cadastrados (filtro opcional `?status=Disponível`) |
| POST | `/livros/cadastrar` | Cadastra um novo livro |
| PUT | `/livros/alterar/{id}` | Altera as informações de um livro |
| GET | `/livros/{id}` | Consulta um livro pelo ID |
| GET | `/documentacao` | Documentação dos endpoints |

### Exemplos

**Página inicial**, `GET /`:

```json
{
  "mensagem": "Bem-vindo ao sistema de gerenciamento da Biblioteca CloudTech Solutions"
}
```

**Cadastrar livro**, `POST /livros/cadastrar`. A resposta é `201 Created` com o livro criado:

```json
{
  "titulo": "Banco de Dados para Iniciantes",
  "autor": "Maria Souza",
  "editora": "EduTech",
  "ano": 2023,
  "categoria": "Informática",
  "status": "Disponível"
}
```

**Alterar livro**, `PUT /livros/alterar/4`:

```json
{
  "titulo": "Banco de Dados Essencial",
  "autor": "Maria Souza",
  "editora": "EduTech",
  "ano": 2024,
  "categoria": "Banco de Dados",
  "status": "Emprestado"
}
```

**Consultar por ID**, `GET /livros/2`:

```json
{
  "id": 2,
  "titulo": "Redes de Computadores",
  "autor": "Carlos Mendes",
  "editora": "Net Books",
  "ano": 2021,
  "categoria": "Redes",
  "status": "Disponível"
}
```

**Documentação**, `GET /documentacao`. Retorna a lista `endpoints` pedida no enunciado e, em `detalhes`, o método HTTP e a descrição de cada endpoint.

### Validações e erros

- Todos os campos são obrigatórios.
- O ano precisa ser 1450 ou maior e não pode passar do ano atual.
- O status precisa ser `Disponível`, `Emprestado` ou `Reservado`. Maiúsculas e acentos não importam: `disponivel` é gravado como `Disponível`.
- Dados inválidos retornam `400`. Livro inexistente retorna `404`. Os erros vêm sempre em JSON:

```json
{
  "status": 404,
  "erro": "Not Found",
  "mensagem": "Livro com id 99 não encontrado",
  "detalhes": [],
  "timestamp": "2026-11-05T21:58:48"
}
```

A aplicação já inicia com três livros de exemplo (IDs 1 a 3). Os dados ficam em memória e voltam ao estado inicial sempre que a aplicação reinicia.

## Estrutura do projeto

```
biblioteca-cloudtech/
├── src/main/java/br/com/cloudtech/biblioteca/
│   ├── config/        # livros de exemplo
│   ├── controller/    # endpoints REST
│   ├── dto/           # objetos de entrada e saída
│   ├── exception/     # tratamento de erros
│   ├── model/         # Livro
│   ├── repository/    # armazenamento em memória
│   └── service/       # regras de negócio
├── src/test/java/...  # testes unitários e de integração
├── thunder-client/    # coleção do Thunder Client
├── Dockerfile
├── render.yaml        # publicação no Render
└── .github/workflows/pipeline.yml
```

## Como executar

Pré-requisitos: Java 21 ou superior e Maven.

```bash
mvn spring-boot:run
```

A aplicação sobe em http://localhost:8080.

### Testes automatizados

```bash
mvn test
```

São 19 testes no total: 8 testes unitários do serviço e 11 testes de integração que cobrem todos os endpoints, incluindo os casos de erro.

### Docker

```bash
docker build -t biblioteca-cloudtech .
docker run -p 8080:8080 biblioteca-cloudtech
```

## Testes com o Thunder Client

1. Instale a extensão **Thunder Client** no VS Code.
2. Em *Collections*, clique em **☰ → Import** e selecione `thunder-client/thunder-collection_biblioteca.json`.
3. Com a aplicação rodando, clique em **Run All**. Cada requisição já traz na aba *Tests* o status HTTP esperado.

## Pipeline CI/CD

O workflow `.github/workflows/pipeline.yml` roda a cada push e a cada pull request, e também pode ser disparado manualmente. Ele tem dois jobs:

1. **Build e testes**: compila com Java 21, executa os testes e publica os relatórios e o `.jar` como artefatos.
2. **Imagem Docker e smoke test**: constrói a imagem, sobe o container e chama os endpoints, incluindo um cadastro e uma alteração.

## Publicação na web

O repositório inclui um `render.yaml` para publicar no [Render](https://render.com) a partir do Dockerfile:

1. No Render, acesse **New → Blueprint** e conecte este repositório do GitHub.
2. O Render lê o `render.yaml`, constrói a imagem e publica a aplicação em uma URL `https://biblioteca-cloudtech-xxxx.onrender.com`.
3. Com `autoDeploy`, cada push na `main` gera uma nova publicação.

A aplicação usa a porta definida na variável `PORT`, que o Render configura automaticamente.

## Branches

| Branch | Conteúdo |
|---|---|
| `feature/livros` | Consulta, cadastro e alteração de livros |
| `feature/documentacao` | Página inicial e endpoint `/documentacao` |
| `feature/testes` | Testes automatizados e coleção do Thunder Client |
| `feature/docker` | Dockerfile e configuração do Render |
| `feature/pipeline-cicd` | Pipeline do GitHub Actions |
