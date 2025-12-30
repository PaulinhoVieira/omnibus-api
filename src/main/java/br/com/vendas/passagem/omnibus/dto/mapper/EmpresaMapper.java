package br.com.vendas.passagem.omnibus.dto.mapper;

import org.springframework.stereotype.Component;

import br.com.vendas.passagem.omnibus.domain.Empresa;
import br.com.vendas.passagem.omnibus.domain.Usuario;
import br.com.vendas.passagem.omnibus.dto.request.EmpresaRequestDTO;
import br.com.vendas.passagem.omnibus.dto.response.EmpresaResponseDTO;

@Component
public class EmpresaMapper {
    
    public Empresa toEntity(EmpresaRequestDTO dto, Usuario usuarioDono) {
        Empresa empresa = new Empresa();
        empresa.setCnpj(dto.cnpj());
        empresa.setNomeFantasia(dto.nomeFantasia());
        empresa.setRazaoSocial(dto.razaoSocial());
        empresa.setUsuarioDono(usuarioDono);
        
        return empresa;
    }

    public EmpresaResponseDTO toResponse(Empresa empresa) {
        return new EmpresaResponseDTO(
            empresa.getId(),
            empresa.getCnpj(),
            empresa.getNomeFantasia(),
            empresa.getRazaoSocial(),
            empresa.getUsuarioDono() != null ? new UsuarioMapper().toDTO(empresa.getUsuarioDono()) : null
        );
    }
}
