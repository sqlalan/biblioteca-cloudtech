package br.com.cloudtech.biblioteca.repository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import br.com.cloudtech.biblioteca.model.Livro;

/**
 * Armazenamento em memoria do acervo de livros.
 */
@Repository
public class LivroRepository {

    private final Map<Long, Livro> livros = new ConcurrentHashMap<>();
    private final AtomicLong sequencia = new AtomicLong();

    public Livro salvar(Livro livro) {
        if (livro.getId() == null) {
            livro.setId(sequencia.incrementAndGet());
        }
        livros.put(livro.getId(), livro);
        return livro;
    }

    public List<Livro> listarTodos() {
        return livros.values().stream()
                .sorted(Comparator.comparing(Livro::getId))
                .toList();
    }

    public Optional<Livro> buscarPorId(Long id) {
        return Optional.ofNullable(livros.get(id));
    }
}
