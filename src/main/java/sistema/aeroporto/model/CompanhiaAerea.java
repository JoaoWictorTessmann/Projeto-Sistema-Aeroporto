package sistema.aeroporto.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import sistema.aeroporto.model.enums.CompanhiaAereaStatus;
import jakarta.validation.constraints.NotBlank;

@Entity
public class CompanhiaAerea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 150)
    private String nome;

    @NotBlank(message = "CNPJ é obrigatório")
    @Column(length = 20)
    private String cnpj;

    private LocalDate dataFundacao;
    private Boolean seguroAeronave;

    @Enumerated(EnumType.STRING)
    private CompanhiaAereaStatus status;

    @OneToMany(mappedBy = "companhia")
    private List<Voo> voos;

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

    public CompanhiaAereaStatus getStatus() {
        return status;
    }

    public void setStatus(CompanhiaAereaStatus status) {
        this.status = status;
    }
}
