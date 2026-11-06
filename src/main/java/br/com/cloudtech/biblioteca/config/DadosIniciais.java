package br.com.cloudtech.biblioteca.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import br.com.cloudtech.biblioteca.dto.LivroRequest;
import br.com.cloudtech.biblioteca.service.LivroService;

/**
 * Carrega alguns livros de exemplo ao iniciar a aplicacao.
 */
@Component
public class DadosIniciais implements CommandLineRunner {

    private final LivroService service;

    public DadosIniciais(LivroService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) {
        service.cadastrar(new LivroRequest("Introdução à Programação", "João Silva", "Tech Books",
                2022, "Tecnologia", "Disponível"));
        service.cadastrar(new LivroRequest("Redes de Computadores", "Carlos Mendes", "Net Books",
                2021, "Redes", "Disponível"));
        service.cadastrar(new LivroRequest("Engenharia de Software Moderna", "Ana Pereira", "Code Press",
                2020, "Tecnologia", "Emprestado"));
    }
}
