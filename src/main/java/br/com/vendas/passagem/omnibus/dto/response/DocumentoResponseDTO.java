package br.com.vendas.passagem.omnibus.dto.response;

import br.com.vendas.passagem.omnibus.domain.enums.TipoDocumento;

public record DocumentoResponseDTO(
    Long id,
    Long usuarioId,
    TipoDocumento tipo,
    String nomeArquivoMinio,
    String contentType
) {}
