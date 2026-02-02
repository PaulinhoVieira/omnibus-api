package br.com.vendas.passagem.omnibus.config.audit;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import br.com.vendas.passagem.omnibus.annotation.Auditable;
import br.com.vendas.passagem.omnibus.service.AuditLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    private static final String EXECUTE = "EXECUTE";

    @Around("@annotation(auditable)")
    public Object auditMethod(ProceedingJoinPoint joinPoint, Auditable auditable) throws Throwable {
        String action = auditable.action();
        String entity = auditable.entity();
        String methodName = joinPoint.getSignature().getName();

        // Se action ou entity vazios, tenta deduzir do nome do método
        if (action.isEmpty()) {
            action = deduceAction(methodName);
        }
        if (entity.isEmpty()) {
            entity = deduceEntity(joinPoint.getTarget().getClass().getSimpleName());
        }

        Object[] args = joinPoint.getArgs();
        String details;
        try {
            details = "Method: " + methodName + " | Args: " + objectMapper.writeValueAsString(args);
        } catch (Exception e) {
            details = "Method: " + methodName + " | Args: " + Arrays.toString(args);
        }

        Object result = null;
        try {
            // Executa o método
            result = joinPoint.proceed();

            // Log de sucesso
            auditLogService.log(action, entity, null, details + " | Status: SUCCESS");
            return result;
        } catch (Exception e) {
            // Log de erro
            auditLogService.log(action, entity, null, details + " | Status: ERROR | Message: " + e.getMessage());
            throw e;
        }
    }

    private String deduceAction(String methodName) {
        if (methodName.startsWith("create") || methodName.startsWith("save")) {
            return "CREATE";
        } else if (methodName.startsWith("update") || methodName.startsWith("edit")) {
            return "UPDATE";
        } else if (methodName.startsWith("delete") || methodName.startsWith("remove")) {
            return "DELETE";
        } else if (methodName.startsWith("find") || methodName.startsWith("get") || methodName.startsWith("list")) {
            return "READ";
        }
        return EXECUTE;
    }

    private String deduceEntity(String className) {
        return className.replace("Service", "").replace("ServiceImpl", "");
    }
}
