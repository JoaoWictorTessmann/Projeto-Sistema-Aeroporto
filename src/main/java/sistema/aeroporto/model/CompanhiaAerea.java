package sistema.aeroporto.model;

import sistema.aeroporto.enums.companhiaAereaStatus;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class CompanhiaAerea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 150)
    private String nome;

    @Column(length = 20)
    private String cnpj;

    private LocalDate dataFundacao;
    private Boolean seguroAeronave;

    @Enumerated(EnumType.STRING)
    private companhiaAereaStatus status;
    
    @ManyToOne
    private Voo companhia;

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

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public LocalDate getDataFundacao() {
        return dataFundacao;
    }

    public void setDataFundacao(LocalDate dataFundacao) {
        this.dataFundacao = dataFundacao;
    }

    public Boolean getSeguroAeronave() {
        return seguroAeronave;
    }

    public void setSeguroAeronave(Boolean seguroAeronave) {
        this.seguroAeronave = seguroAeronave;
    }

    public companhiaAereaStatus getStatus() {
        return status;
    }

    public void setStatus(companhiaAereaStatus status) {
        this.status = status;
    }
}
