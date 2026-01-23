package br.com.vendas.passagem.omnibus.domain;

import lombok.*;

import java.time.LocalDateTime;

import br.com.vendas.passagem.omnibus.domain.enums.TipoPerfil;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario implements UserDetails {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @OneToMany(mappedBy = "usuarioDono", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Empresa> empresas;

    @OneToMany(mappedBy = "passageiro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Passagen> passagens;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Documento documento;

    // Métodos utilitários para gerenciar perfis
    public boolean possuiPerfil(TipoPerfil perfil) {
        return perfis.contains(perfil);
    }

    public void adicionarPerfil(TipoPerfil perfil) {
        this.perfis.add(perfil);
    }

    public void removerPerfil(TipoPerfil perfil) {
        this.perfis.remove(perfil);
    }

    // configuraçoes de segurança

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return perfis.stream()
                .map(perfil -> new SimpleGrantedAuthority("ROLE_" + perfil.name()))
                .toList();
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
