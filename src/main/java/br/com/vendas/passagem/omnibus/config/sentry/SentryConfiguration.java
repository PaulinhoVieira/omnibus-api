package br.com.vendas.passagem.omnibus.config.sentry;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import io.sentry.Sentry;
import io.sentry.SentryOptions;
import io.sentry.spring.jakarta.SentryUserProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Configuração customizada do Sentry para monitoramento de erros.
 * 
 * Esta classe configura callbacks e filtros para enriquecer os eventos
 * enviados ao Sentry com informações contextuais da aplicação.
 */
@Configuration
public class SentryConfiguration {

    /**
     * Configura o Sentry antes da inicialização.
     * Adiciona informações customizadas aos eventos.
     */
    @Bean
    public SentryOptions.BeforeSendCallback beforeSendCallback() {
        return (event, hint) -> {
            // Adicionar tags customizadas a todos os eventos
            event.setTag("application", "omnibus-api");
            event.setTag("module", "authentication");
            
            // Adicionar contexto adicional
            event.setExtra("javaVersion", System.getProperty("java.version"));
            event.setExtra("osName", System.getProperty("os.name"));
            
            return event;
        };
    }

    /**
     * Provedor customizado de informações do usuário para o Sentry.
     * Captura informações do usuário autenticado quando disponível.
     */
    @Bean
    public SentryUserProvider sentryUserProvider() {
        return () -> {
            // Aqui você pode capturar o usuário autenticado do SecurityContext
            // Por exemplo: Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            // Por enquanto, retornamos null (Sentry usará IP como identificador)
            return null;
        };
    }

    /**
     * Interceptor para adicionar breadcrumbs em cada requisição HTTP.
     * Breadcrumbs ajudam a entender o fluxo que levou ao erro.
     */
    @Bean
    public HandlerInterceptor sentryBreadcrumbInterceptor() {
        return new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
                // Adicionar breadcrumb para cada requisição
                Sentry.addBreadcrumb(
                    String.format("%s %s", request.getMethod(), request.getRequestURI()),
                    "http.request"
                );
                return true;
            }
        };
    }
}
