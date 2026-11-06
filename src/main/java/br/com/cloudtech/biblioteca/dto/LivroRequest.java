package br.com.cloudtech.biblioteca.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Dados enviados para cadastrar ou alterar um livro.
 */
public record LivroRequest(
        @NotBlank(message = "o título é obrigatório")
        String titulo,

        @NotBlank(message = "o autor é obrigatório")
        String autor,

        @NotBlank(message = "a editora é obrigatória")
        String editora,

        @NotNull(message = "o ano é obrigatório")
        @Min(value = 1450, message = "o ano deve ser maior ou igual a 1450")
        Integer ano,

        @NotBlank(message = "a categoria é obrigatória")
        String categoria,

        @NotBlank(message = "o status é obrigatório")
        @Pattern(regexp = "(?i)dispon[ií]vel|emprestado|reservado",
                 message = "o status deve ser Disponível, Emprestado ou Reservado")
        String status) {
}
