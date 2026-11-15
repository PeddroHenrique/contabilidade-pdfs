package br.com.projeto.contabilidade.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Column;

import java.util.List;

@Entity
public class Cliente {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(columnDefinition = "varchar(255)")
    private String nome;
    
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Arquivo> arquivos;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Arquivo> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<Arquivo> arquivos) {
        this.arquivos = arquivos;
    }
    
}
