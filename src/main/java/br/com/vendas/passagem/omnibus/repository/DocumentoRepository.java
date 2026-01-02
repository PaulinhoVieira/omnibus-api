package br.com.vendas.passagem.omnibus.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.vendas.passagem.omnibus.domain.Documento;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {
}
