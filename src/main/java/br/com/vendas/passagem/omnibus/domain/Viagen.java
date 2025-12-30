package br.com.vendas.passagem.omnibus.domain;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


import jakarta.persistence.*;

@Entity
@Table(name = "viagens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Viagen {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(name = "origem", nullable = false)
    private String origem;

    @Column(name = "destino", nullable = false)
    private String destino;

    @Column(name = "data_partida", nullable = false)
    private LocalDateTime dataPartida;

    @Column(name = "preco", nullable = false)
    private BigDecimal preco;

    @Column(name = "vagas_totais", nullable = false)
    private Integer vagasTotais;

    @Column(name = "vagas_disponiveis", nullable = false)
    private Integer vagasDisponiveis;

    @Column(name = "possui_assento_marcado", nullable = false)
    private Boolean possuiAssentoMarcado = true;
}
