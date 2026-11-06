package br.com.cloudtech.biblioteca.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.cloudtech.biblioteca.dto.LivroRequest;
import br.com.cloudtech.biblioteca.model.Livro;
import br.com.cloudtech.biblioteca.service.LivroService;
import jakarta.validation.Valid;

/**
 * Endpoints de consulta, cadastro e alteracao de livros.
 */
@RestController
@RequestMapping("/livros")
public class LivroController {

    private final LivroService service;

    public LivroController(LivroService service) {
        this.service = service;
    }

    /** b) Consultar livros. Filtro opcional: /livros?status=Disponível */
    @GetMapping
    public List<Livro> listar(@RequestParam(required = false) String status) {
        return service.listar(status);
    }

    /** e) Consultar livro por ID. */
    @GetMapping("/{id}")
    public Livro buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    /** c) Cadastrar novo livro. */
    @PostMapping("/cadastrar")
    public ResponseEntity<Livro> cadastrar(@Valid @RequestBody LivroRequest request) {
        Livro livro = service.cadastrar(request);
        return ResponseEntity.created(URI.create("/livros/" + livro.getId())).body(livro);
    }

    /** d) Alterar informacoes de um livro. */
    @PutMapping("/alterar/{id}")
    public Livro alterar(@PathVariable Long id, @Valid @RequestBody LivroRequest request) {
        return service.alterar(id, request);
    }
}
