package br.com.vendas.passagem.omnibus.exception;

/**
 * Exceção lançada quando um DTO possui dados inválidos ou inconsistentes.
 */
public class InvalidDtoException extends RuntimeException {

    public InvalidDtoException(String message) {
        super(message);
    }

    public InvalidDtoException(String fieldName, String reason) {
        super(String.format("Campo inválido '%s': %s", fieldName, reason));
    }

    public InvalidDtoException(String message, Throwable cause) {
        super(message, cause);
    }
}
