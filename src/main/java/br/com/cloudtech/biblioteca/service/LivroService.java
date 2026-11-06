package br.com.cloudtech.biblioteca.service;

import java.text.Normalizer;
import java.time.Year;
import java.util.List;

import org.springframework.stereotype.Service;

import br.com.cloudtech.biblioteca.dto.LivroRequest;
import br.com.cloudtech.biblioteca.exception.RecursoNaoEncontradoException;
import br.com.cloudtech.biblioteca.exception.RegraNegocioException;
import br.com.cloudtech.biblioteca.model.Livro;
import br.com.cloudtech.biblioteca.repository.LivroRepository;

/**
 * Regras de negocio do gerenciamento do acervo.
 */
@Service
public class LivroService {

    private final LivroRepository repository;

    public LivroService(LivroRepository repository) {
        this.repository = repository;
    }

    /**
     * Lista os livros do acervo. Se um status for informado (ex.: "Disponível"), filtra por ele.
     * A comparacao ignora maiusculas/minusculas e acentos.
     */
    public List<Livro> listar(String status) {
        List<Livro> todos = repository.listarTodos();
        if (status == null || status.isBlank()) {
            return todos;
        }
        String filtro = normalizar(status);
        return todos.stream()
                .filter(livro -> normalizar(livro.getStatus()).equals(filtro))
                .toList();
    }

    public Livro buscarPorId(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Livro com id " + id + " não encontrado"));
    }

    public Livro cadastrar(LivroRequest request) {
        validarAno(request.ano());
        Livro livro = new Livro();
        preencher(livro, request);
        return repository.salvar(livro);
    }

    public Livro alterar(Long id, LivroRequest request) {
        Livro livro = buscarPorId(id);
        validarAno(request.ano());
        preencher(livro, request);
        return repository.salvar(livro);
    }

    private void preencher(Livro livro, LivroRequest request) {
        livro.setTitulo(request.titulo().trim());
        livro.setAutor(request.autor().trim());
        livro.setEditora(request.editora().trim());
        livro.setAno(request.ano());
        livro.setCategoria(request.categoria().trim());
        livro.setStatus(padronizarStatus(request.status()));
    }

    private void validarAno(Integer ano) {
        int anoAtual = Year.now().getValue();
        if (ano > anoAtual) {
            throw new RegraNegocioException("O ano não pode ser maior que o ano atual (" + anoAtual + ")");
        }
    }

    /** Grava o status sempre com a mesma grafia: Disponível, Emprestado ou Reservado. */
    private static String padronizarStatus(String status) {
        return switch (normalizar(status)) {
            case "disponivel" -> "Disponível";
            case "emprestado" -> "Emprestado";
            case "reservado" -> "Reservado";
            default -> status.trim();
        };
    }

    private static String normalizar(String texto) {
        return Normalizer.normalize(texto.trim().toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }
}
