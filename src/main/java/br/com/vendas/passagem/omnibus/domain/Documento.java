package br.com.vendas.passagem.omnibus.domain;

import lombok.*;

import java.time.LocalDateTime;

import br.com.vendas.passagem.omnibus.domain.enums.TipoDocumento;
import jakarta.persistence.*;

@Entity
@Table(name = "documentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Documento {
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false)
    private TipoDocumento tipo; // e.g., 'RG', 'CNH', 'PASSAPORTE'

    @Column(name = "nome_arquivo_minio", nullable = false)
    private String nomeArquivoMinio;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "validado")
    private Boolean validado = false;

    @Column(name = "data_upload")
    private LocalDateTime dataUpload = LocalDateTime.now();
}