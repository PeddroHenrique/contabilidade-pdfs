package br.com.projeto.contabilidade.repository;

import br.com.projeto.contabilidade.model.Arquivo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArquivoRepository extends JpaRepository<Arquivo, Long>{
    Page<Arquivo> findAllByOrderByIdDesc(Pageable pageable);
    Page<Arquivo> findByClienteNomeOrderByIdDesc(String nome, Pageable pageable);
    List<Arquivo> findAllByClienteId(Long id);
}
