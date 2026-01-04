package br.com.vendas.passagem.omnibus.exception;

/**
 * Exceção lançada quando há problemas na validação de arquivos.
 */
public class FileValidationException extends RuntimeException {

    public FileValidationException(String message) {
        super(message);
    }

    public FileValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static FileValidationException invalidFileType(String fileName, String allowedTypes) {
        return new FileValidationException(
            String.format("Tipo de arquivo inválido para '%s'. Tipos permitidos: %s", fileName, allowedTypes)
        );
    }

    public static FileValidationException fileTooLarge(String fileName, long maxSize) {
        return new FileValidationException(
            String.format("Arquivo '%s' excede o tamanho máximo permitido de %d bytes", fileName, maxSize)
        );
    }

    public static FileValidationException emptyFile(String fileName) {
        return new FileValidationException(
            String.format("Arquivo '%s' está vazio", fileName)
        );
    }
}
