package br.com.cloudtech.biblioteca.controller;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Testes de integracao de todos os endpoints da aplicacao.
 */
@SpringBootTest
@AutoConfigureMockMvc
class EndpointsTest {

    private static final String LIVRO_JSON = """
            {
              "titulo": "Banco de Dados para Iniciantes",
              "autor": "Maria Souza",
              "editora": "EduTech",
              "ano": 2023,
              "categoria": "Informática",
              "status": "Disponível"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET / deve exibir a mensagem de boas-vindas")
    void paginaInicial() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem")
                        .value("Bem-vindo ao sistema de gerenciamento da Biblioteca CloudTech Solutions"));
    }

    @Test
    @DisplayName("GET /livros deve listar os livros cadastrados")
    void listarLivros() throws Exception {
        mockMvc.perform(get("/livros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(3)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Introdução à Programação"))
                .andExpect(jsonPath("$[0].autor").value("João Silva"));
    }

    @Test
    @DisplayName("GET /livros?status=Disponível deve retornar apenas livros disponíveis")
    void listarDisponiveis() throws Exception {
        mockMvc.perform(get("/livros").param("status", "Disponível"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].status").value(everyItem(is("Disponível"))));
    }

    @Test
    @DisplayName("GET /livros/{id} deve retornar o livro correspondente")
    void consultarPorId() throws Exception {
        mockMvc.perform(get("/livros/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.titulo").value("Redes de Computadores"))
                .andExpect(jsonPath("$.autor").value("Carlos Mendes"))
                .andExpect(jsonPath("$.editora").value("Net Books"))
                .andExpect(jsonPath("$.ano").value(2021))
                .andExpect(jsonPath("$.categoria").value("Redes"));
    }

    @Test
    @DisplayName("GET /livros/{id} inexistente deve retornar 404")
    void consultarInexistente() throws Exception {
        mockMvc.perform(get("/livros/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Livro com id 9999 não encontrado"));
    }

    @Test
    @DisplayName("GET /livros/abc deve retornar 400")
    void consultarIdInvalido() throws Exception {
        mockMvc.perform(get("/livros/abc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /livros/cadastrar deve cadastrar um novo livro")
    void cadastrar() throws Exception {
        mockMvc.perform(post("/livros/cadastrar").contentType(MediaType.APPLICATION_JSON).content(LIVRO_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.titulo").value("Banco de Dados para Iniciantes"))
                .andExpect(jsonPath("$.status").value("Disponível"));

        mockMvc.perform(get("/livros"))
                .andExpect(jsonPath("$[*].titulo").value(hasItem("Banco de Dados para Iniciantes")));
    }

    @Test
    @DisplayName("POST /livros/cadastrar com dados inválidos deve retornar 400")
    void cadastrarInvalido() throws Exception {
        String json = """
                {"titulo": "", "autor": "Maria Souza", "ano": 1200, "status": "Perdido"}
                """;

        mockMvc.perform(post("/livros/cadastrar").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Dados inválidos"))
                .andExpect(jsonPath("$.detalhes.length()").value(5));
    }

    @Test
    @DisplayName("PUT /livros/alterar/{id} deve alterar as informações do livro")
    void alterar() throws Exception {
        long id = cadastrarLivro();
        String json = """
                {
                  "titulo": "Banco de Dados Essencial",
                  "autor": "Maria Souza",
                  "editora": "EduTech",
                  "ano": 2024,
                  "categoria": "Banco de Dados",
                  "status": "Emprestado"
                }
                """;

        mockMvc.perform(put("/livros/alterar/" + id).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.titulo").value("Banco de Dados Essencial"))
                .andExpect(jsonPath("$.ano").value(2024))
                .andExpect(jsonPath("$.status").value("Emprestado"));

        mockMvc.perform(get("/livros/" + id))
                .andExpect(jsonPath("$.categoria").value("Banco de Dados"));
    }

    @Test
    @DisplayName("PUT /livros/alterar/{id} de livro inexistente deve retornar 404")
    void alterarInexistente() throws Exception {
        mockMvc.perform(put("/livros/alterar/9999").contentType(MediaType.APPLICATION_JSON).content(LIVRO_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /documentacao deve listar os endpoints da aplicação")
    void documentacao() throws Exception {
        mockMvc.perform(get("/documentacao"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.endpoints.length()").value(6))
                .andExpect(jsonPath("$.endpoints[0]").value("/"))
                .andExpect(jsonPath("$.endpoints[1]").value("/livros"))
                .andExpect(jsonPath("$.endpoints[2]").value("/livros/cadastrar"))
                .andExpect(jsonPath("$.endpoints[3]").value("/livros/alterar/{id}"))
                .andExpect(jsonPath("$.endpoints[4]").value("/livros/{id}"))
                .andExpect(jsonPath("$.endpoints[5]").value("/documentacao"));
    }

    private long cadastrarLivro() throws Exception {
        MvcResult resultado = mockMvc.perform(post("/livros/cadastrar")
                        .contentType(MediaType.APPLICATION_JSON).content(LIVRO_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(resultado.getResponse().getContentAsString()).get("id").asLong();
    }
}
