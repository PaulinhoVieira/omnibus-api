package br.com.vendas.passagem.omnibus.domain;

import lombok.*;

import java.util.UUID;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "empresas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Empresa {
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_dono_id", nullable = false)
    private Usuario usuarioDono;

    @Column(name = "cnpj", unique = true, nullable = false)
    private String cnpj;

    @Column(name = "nome_fantasia", nullable = false)
    private String nomeFantasia;

    @Column(name = "razao_social")
    private String razaoSocial;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL)
    private List<Viagen> viagens;
}
