package br.com.vendas.passagem.omnibus.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca um método de serviço para auditoria automática.
 * O AspectJ irá interceptar e registrar quem executou, quando e com quais argumentos.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
    /**
     * Tipo de operação: CREATE, READ, UPDATE, DELETE, ou outro.
     */
    String action() default "";

    /**
     * Nome da entidade sendo operada.
     */
    String entity() default "";
}
