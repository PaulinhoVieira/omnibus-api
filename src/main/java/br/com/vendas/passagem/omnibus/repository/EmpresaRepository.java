package br.com.vendas.passagem.omnibus.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.vendas.passagem.omnibus.domain.Empresa;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    
}
