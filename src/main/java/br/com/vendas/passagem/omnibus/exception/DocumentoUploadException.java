package br.com.vendas.passagem.omnibus.exception;

/**
 * Exceção lançada quando falha o upload de um documento.
 */
public class DocumentoUploadException extends RuntimeException {

    public DocumentoUploadException(String message) {
        super(message);
    }

    public DocumentoUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}
