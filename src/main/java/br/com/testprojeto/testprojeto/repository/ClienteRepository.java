/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package br.com.testprojeto.testprojeto.repository;

import br.com.testprojeto.testprojeto.model.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author PEDRO
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>{
    Page<Cliente> findAllByUserUsername(String username, Pageable pageable);
    List<Cliente> findAllByUserUsername(String username);
}
