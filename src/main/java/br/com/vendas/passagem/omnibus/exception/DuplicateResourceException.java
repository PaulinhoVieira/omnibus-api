package br.com.vendas.passagem.omnibus.exception;

/**
 * Exceção lançada quando há uma tentativa de criar/atualizar um recurso com dados duplicados.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
