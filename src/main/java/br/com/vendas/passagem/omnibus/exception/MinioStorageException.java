package br.com.vendas.passagem.omnibus.exception;

/**
 * Exceção lançada quando ocorre um erro relacionado ao armazenamento no MinIO.
 */
public class MinioStorageException extends RuntimeException {

    public MinioStorageException(String message) {
        super(message);
    }

    public MinioStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public static MinioStorageException uploadFailed(String fileName, Throwable cause) {
        return new MinioStorageException(
            String.format("Falha ao fazer upload do arquivo: %s", fileName), 
            cause
        );
    }

    public static MinioStorageException downloadFailed(String fileName, Throwable cause) {
        return new MinioStorageException(
            String.format("Falha ao fazer download do arquivo: %s", fileName), 
            cause
        );
    }

    public static MinioStorageException deleteFailed(String fileName, Throwable cause) {
        return new MinioStorageException(
            String.format("Falha ao deletar o arquivo: %s", fileName), 
            cause
        );
    }

    public static MinioStorageException bucketCreationFailed(String bucketName, Throwable cause) {
        return new MinioStorageException(
            String.format("Falha ao criar o bucket: %s", bucketName), 
            cause
        );
    }

    public static MinioStorageException bucketAccessFailed(String bucketName, Throwable cause) {
        return new MinioStorageException(
            String.format("Falha ao acessar o bucket: %s", bucketName), 
            cause
        );
    }
}
