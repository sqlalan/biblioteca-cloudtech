package br.com.cloudtech.biblioteca.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.cloudtech.biblioteca.dto.DocumentacaoResponse;
import br.com.cloudtech.biblioteca.dto.DocumentacaoResponse.Endpoint;
import br.com.cloudtech.biblioteca.dto.MensagemResponse;

/**
 * Pagina inicial e documentacao dos endpoints.
 */
@RestController
public class HomeController {

    private static final List<Endpoint> ENDPOINTS = List.of(
            new Endpoint("GET", "/", "Mensagem de boas-vindas ao sistema da biblioteca"),
            new Endpoint("GET", "/livros", "Lista os livros cadastrados (filtro opcional ?status=Disponível)"),
            new Endpoint("POST", "/livros/cadastrar", "Cadastra um novo livro"),
            new Endpoint("PUT", "/livros/alterar/{id}", "Altera as informações de um livro já cadastrado"),
            new Endpoint("GET", "/livros/{id}", "Consulta um livro pelo seu código de identificação"),
            new Endpoint("GET", "/documentacao", "Documentação dos endpoints da aplicação"));

    /** a) Pagina inicial. */
    @GetMapping("/")
    public MensagemResponse paginaInicial() {
        return new MensagemResponse("Bem-vindo ao sistema de gerenciamento da Biblioteca CloudTech Solutions");
    }

    /** f) Documentacao dos endpoints. */
    @GetMapping("/documentacao")
    public DocumentacaoResponse documentacao() {
        List<String> caminhos = ENDPOINTS.stream().map(Endpoint::endpoint).toList();
        return new DocumentacaoResponse(caminhos, ENDPOINTS);
    }
}
