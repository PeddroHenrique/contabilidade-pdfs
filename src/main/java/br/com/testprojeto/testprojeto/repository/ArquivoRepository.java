/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.testprojeto.testprojeto.repository;

import br.com.testprojeto.testprojeto.model.Arquivo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author PEDRO
 */
@Repository
public interface ArquivoRepository extends JpaRepository<Arquivo, Long>{
    Page<Arquivo> findAllByOrderByDataCriacaoDesc(Pageable pageable);
    Page<Arquivo> findAllByClienteNomeOrderByDataCriacaoDesc(String nome, Pageable pageable);
}
