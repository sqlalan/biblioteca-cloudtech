package br.com.cloudtech.biblioteca.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Year;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.cloudtech.biblioteca.dto.LivroRequest;
import br.com.cloudtech.biblioteca.exception.RecursoNaoEncontradoException;
import br.com.cloudtech.biblioteca.exception.RegraNegocioException;
import br.com.cloudtech.biblioteca.model.Livro;
import br.com.cloudtech.biblioteca.repository.LivroRepository;

/**
 * Testes unitarios das regras de negocio do acervo.
 */
class LivroServiceTest {

    private LivroService service;

    @BeforeEach
    void configurar() {
        service = new LivroService(new LivroRepository());
    }

    private LivroRequest livro(String titulo, int ano, String status) {
        return new LivroRequest(titulo, "Maria Souza", "EduTech", ano, "Informática", status);
    }

    @Test
    @DisplayName("Deve cadastrar livros gerando ids sequenciais")
    void deveCadastrar() {
        Livro primeiro = service.cadastrar(livro("Banco de Dados para Iniciantes", 2023, "Disponível"));
        Livro segundo = service.cadastrar(livro("Java Básico", 2020, "Emprestado"));

        assertEquals(1L, primeiro.getId());
        assertEquals(2L, segundo.getId());
        assertEquals("Banco de Dados para Iniciantes", primeiro.getTitulo());
        assertEquals(2023, primeiro.getAno());
    }

    @Test
    @DisplayName("Deve padronizar a grafia do status")
    void devePadronizarStatus() {
        Livro livro = service.cadastrar(livro("Java Básico", 2020, "disponivel"));

        assertEquals("Disponível", livro.getStatus());
    }

    @Test
    @DisplayName("Deve listar todos os livros e filtrar os disponíveis")
    void deveListarEFiltrar() {
        service.cadastrar(livro("Livro A", 2020, "Disponível"));
        service.cadastrar(livro("Livro B", 2021, "Emprestado"));
        service.cadastrar(livro("Livro C", 2022, "Disponível"));

        assertEquals(3, service.listar(null).size());
        assertEquals(2, service.listar("Disponível").size());
        assertEquals(2, service.listar("disponivel").size());
        assertEquals(1, service.listar("EMPRESTADO").size());
    }

    @Test
    @DisplayName("Deve consultar um livro pelo id")
    void deveBuscarPorId() {
        service.cadastrar(livro("Livro A", 2020, "Disponível"));
        Livro redes = service.cadastrar(livro("Redes de Computadores", 2021, "Disponível"));

        assertEquals("Redes de Computadores", service.buscarPorId(redes.getId()).getTitulo());
    }

    @Test
    @DisplayName("Deve lançar exceção ao consultar livro inexistente")
    void deveLancarExcecaoLivroInexistente() {
        RecursoNaoEncontradoException ex = assertThrows(RecursoNaoEncontradoException.class,
                () -> service.buscarPorId(42L));

        assertEquals("Livro com id 42 não encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("Deve alterar as informações de um livro existente")
    void deveAlterar() {
        Livro livro = service.cadastrar(livro("Banco de Dados para Iniciantes", 2023, "Disponível"));

        Livro alterado = service.alterar(livro.getId(), new LivroRequest("Banco de Dados Essencial",
                "Maria Souza", "EduTech", 2024, "Banco de Dados", "Emprestado"));

        assertEquals(livro.getId(), alterado.getId());
        assertEquals("Banco de Dados Essencial", alterado.getTitulo());
        assertEquals(2024, alterado.getAno());
        assertEquals("Banco de Dados", alterado.getCategoria());
        assertEquals("Emprestado", alterado.getStatus());
        assertEquals(1, service.listar(null).size());
    }

    @Test
    @DisplayName("Não deve alterar livro inexistente")
    void naoDeveAlterarInexistente() {
        assertThrows(RecursoNaoEncontradoException.class,
                () -> service.alterar(99L, livro("Qualquer", 2020, "Disponível")));
    }

    @Test
    @DisplayName("Não deve aceitar ano maior que o ano atual")
    void naoDeveAceitarAnoFuturo() {
        int anoFuturo = Year.now().getValue() + 1;

        assertThrows(RegraNegocioException.class,
                () -> service.cadastrar(livro("Livro do Futuro", anoFuturo, "Disponível")));
    }
}
