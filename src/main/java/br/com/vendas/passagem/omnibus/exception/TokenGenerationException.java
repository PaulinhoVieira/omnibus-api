package br.com.vendas.passagem.omnibus.exception;

/**
 * Exceção lançada quando a geração do token JWT falha.
 */
public class TokenGenerationException extends RuntimeException {

    public TokenGenerationException(String message) {
        super(message);
    }

    public TokenGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
