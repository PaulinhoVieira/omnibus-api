package br.com.vendas.passagem.omnibus.domain;

import lombok.*;

import java.io.ObjectInputFilter.Status;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;

@Entity
@Table(name = "passagens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Passagen {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "viagem_id", nullable = false)
    private Viagen viagem; 

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "passageiro_id", nullable = false)
    private Usuario passageiro;

    @Column(name = "data_compra")
    private LocalDateTime dataCompra = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status; // 'PENDENTE', 'PAGA', 'CANCELADA

    @Column(name = "poltrona")
    private Integer poltrona; // opcional

    @Column(name = "valor_pago", nullable = false)
    private BigDecimal valorPago;

}
