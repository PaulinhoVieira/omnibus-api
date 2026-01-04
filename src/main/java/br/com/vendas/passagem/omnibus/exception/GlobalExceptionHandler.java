package br.com.vendas.passagem.omnibus.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import br.com.vendas.passagem.omnibus.dto.response.ErrorResponseDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

/**
 * Manipulador global de exceções para a API.
 * Captura e trata todas as exceções personalizadas e padrões do Spring.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata exceções de recurso não encontrado.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFoundException(
            ResourceNotFoundException ex, 
            WebRequest request) {
        
        ErrorResponseDTO error = new ErrorResponseDTO(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Trata exceções relacionadas ao MinIO.
     */
    @ExceptionHandler(MinioStorageException.class)
    public ResponseEntity<ErrorResponseDTO> handleMinioStorageException(
            MinioStorageException ex, 
            WebRequest request) {
        
        ErrorResponseDTO error = new ErrorResponseDTO(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Storage Error",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Trata exceções de DTO inválido.
     */
    @ExceptionHandler(InvalidDtoException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidDtoException(
            InvalidDtoException ex, 
            WebRequest request) {
        
        ErrorResponseDTO error = new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid Data",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata exceções de regra de negócio.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessException(
            BusinessException ex, 
            WebRequest request) {
        
        ErrorResponseDTO error = new ErrorResponseDTO(
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            "Business Rule Violation",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    /**
     * Trata exceções de recurso duplicado.
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponseDTO> handleDuplicateResourceException(
            DuplicateResourceException ex, 
            WebRequest request) {
        
        ErrorResponseDTO error = new ErrorResponseDTO(
            HttpStatus.CONFLICT.value(),
            "Duplicate Resource",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Trata exceções de violação de integridade de dados (constraints do banco).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, 
            WebRequest request) {
        
        String message = "Erro ao processar os dados";
        
        // Verifica se é erro de chave duplicada
        if (ex.getMessage() != null && ex.getMessage().contains("duplicate key")) {
            if (ex.getMessage().contains("usuarios_email_key")) {
                message = "Este email já está cadastrado no sistema";
            } else if (ex.getMessage().contains("usuarios_cpf_key")) {
                message = "Este CPF já está cadastrado no sistema";
            } else {
                message = "Dados duplicados: um ou mais valores já existem no sistema";
            }
        }
        
        ErrorResponseDTO error = new ErrorResponseDTO(
            HttpStatus.CONFLICT.value(),
            "Data Integrity Violation",
            message,
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Trata exceções de validação de arquivo.
     */
    @ExceptionHandler(FileValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleFileValidationException(
            FileValidationException ex, 
            WebRequest request) {
        
        ErrorResponseDTO error = new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "File Validation Error",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata exceções de validação do Bean Validation (javax.validation).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, 
            WebRequest request) {
        
        ErrorResponseDTO error = new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Error",
            "Erro de validação nos campos",
            request.getDescription(false).replace("uri=", "")
        );
        
        // Adiciona os erros de cada campo
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            error.addFieldError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata exceções de violação de constraints.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolationException(
            ConstraintViolationException ex, 
            WebRequest request) {
        
        ErrorResponseDTO error = new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Constraint Violation",
            "Violação de restrições de validação",
            request.getDescription(false).replace("uri=", "")
        );
        
        // Adiciona os erros de cada constraint
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            error.addFieldError(propertyPath, message);
        }
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata exceções de arquivo muito grande.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponseDTO> handleMaxUploadSizeExceeded(
            MaxUploadSizeExceededException ex, 
            WebRequest request) {
        
        ErrorResponseDTO error = new ErrorResponseDTO(
            HttpStatus.PAYLOAD_TOO_LARGE.value(),
            "File Too Large",
            "O arquivo enviado excede o tamanho máximo permitido",
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(error);
    }

    /**
     * Trata exceções de argumento ilegal.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(
            IllegalArgumentException ex, 
            WebRequest request) {
        
        ErrorResponseDTO error = new ErrorResponseDTO(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid Argument",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Trata todas as exceções não tratadas especificamente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGlobalException(
            Exception ex, 
            WebRequest request) {
        
        ErrorResponseDTO error = new ErrorResponseDTO(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "Ocorreu um erro interno no servidor. Por favor, tente novamente mais tarde.",
            request.getDescription(false).replace("uri=", "")
        );
        
        // Log da exceção para debug (você pode usar um logger aqui)
        ex.printStackTrace();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
