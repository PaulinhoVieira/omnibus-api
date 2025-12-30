package br.com.vendas.passagem.omnibus.domain;

import lombok.*;

import jakarta.persistence.*;

@Entity
@Table(name = "perfis_usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PerfisUsuario {

    @EmbeddedId
    private PerfilUsuarioId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("usuarioId")
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}
