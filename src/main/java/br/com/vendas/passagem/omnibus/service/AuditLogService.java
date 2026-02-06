package br.com.vendas.passagem.omnibus.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.com.vendas.passagem.omnibus.domain.audit.AuditLog;
import br.com.vendas.passagem.omnibus.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuditLog log(String action, String entityName, Long entityId, String details) {
        try {
            AuditLog logEntry = AuditLog.builder()
                    .action(action)
                    .entityName(entityName)
                    .entityId(entityId)
                    .details(details)
                    .username(resolveUsername())
                    .build();

            return auditLogRepository.save(logEntry);
        } catch (Exception e) {
            log.warn("Falha ao registrar auditoria: {} | {}", action, entityName, e);
            return null;
        }
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
