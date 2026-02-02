package br.com.vendas.passagem.omnibus.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.vendas.passagem.omnibus.domain.audit.AuditLog;
import br.com.vendas.passagem.omnibus.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLog log(String action, String entityName, Long entityId, String details) {
        AuditLog logEntry = AuditLog.builder()
                .action(action)
                .entityName(entityName)
                .entityId(entityId)
                .details(details)
                .username(resolveUsername())
                .build();

        return auditLogRepository.save(logEntry);
    }

    private String resolveUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "system";
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof String principalName) {
            return principalName;
        }

        return authentication.getName();
    }
}
