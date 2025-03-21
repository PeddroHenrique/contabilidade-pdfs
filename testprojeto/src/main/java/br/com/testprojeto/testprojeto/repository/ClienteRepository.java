/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package br.com.testprojeto.testprojeto.repository;

import br.com.testprojeto.testprojeto.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author PEDRO
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>{
    
}
