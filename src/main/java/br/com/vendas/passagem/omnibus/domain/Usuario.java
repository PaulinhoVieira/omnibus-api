package br.com.vendas.passagem.omnibus.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

import br.com.vendas.passagem.omnibus.domain.enums.TipoPerfil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "senha", nullable = false)
    private String senha;

    @Column(name = "cpf", unique = true, nullable = false)
    private String cpf;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "perfis_usuario", joinColumns = @JoinColumn(name = "usuario_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "perfil")
    private Set<TipoPerfil> perfis = new HashSet<>();

    @OneToMany(mappedBy = "usuarioDono", cascade = CascadeType.ALL)
    private List<Empresa> empresas;

    @OneToMany(mappedBy = "passageiro", cascade = CascadeType.ALL)
    private List<Passagen> passagens;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    private Documento documento;

    // Método utilitário para o seu Use Case
    public boolean possuiPerfil(TipoPerfil perfil) {
        return perfis.contains(perfil);
    }
}
