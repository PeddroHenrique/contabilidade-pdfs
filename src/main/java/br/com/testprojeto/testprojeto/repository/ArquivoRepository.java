/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package br.com.testprojeto.testprojeto.repository;

import br.com.testprojeto.testprojeto.model.Arquivo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author PEDRO
 */
@Repository
public interface ArquivoRepository extends JpaRepository<Arquivo, Long>{
    Page<Arquivo> findAllByOrderByDataCriacaoDesc(Pageable pageable);
    @Query("SELECT a FROM Arquivo a WHERE a.cliente.user.username = :username ORDER BY a.dataCriacao DESC")
    Page<Arquivo> findArquivosByClienteUsuario(@Param("username") String username, Pageable pageable);
    @Query("SELECT a FROM Arquivo a WHERE a.cliente.nome = :nome AND a.cliente.user.username = :username ORDER BY a.dataCriacao DESC")
    Page<Arquivo> findArquivosByClienteNomeAndUsuario(@Param("nome") String nome, @Param("username") String username, Pageable pageable);
    List<Arquivo> findAllByClienteId(Long id);
}
