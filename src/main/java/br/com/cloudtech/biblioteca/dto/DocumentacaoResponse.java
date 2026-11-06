package br.com.cloudtech.biblioteca.dto;

import java.util.List;

/**
 * Documentacao dos endpoints: a lista simples pedida no enunciado
 * e, em "detalhes", o metodo HTTP e a descricao de cada um.
 */
public record DocumentacaoResponse(List<String> endpoints, List<Endpoint> detalhes) {

    public record Endpoint(String metodo, String endpoint, String descricao) {
    }
}
