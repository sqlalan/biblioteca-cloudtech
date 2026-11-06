package br.com.cloudtech.biblioteca.exception;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Formato padrao das respostas de erro da API.
 */
public record ErroResponse(
        int status,
        String erro,
        String mensagem,
        List<String> detalhes,
        LocalDateTime timestamp) {

    public static ErroResponse of(int status, String erro, String mensagem, List<String> detalhes) {
        return new ErroResponse(status, erro, mensagem, detalhes,
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    }
}
