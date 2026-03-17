package sistema.aeroporto.model;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;
import sistema.aeroporto.model.enums.PilotoStatus;

@Entity
public class Piloto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 150)
    private String nome;

    private Integer idade;

    @Column(length = 1)
    private String genero;

    @Column(length = 11)
    private String cpf;

    private LocalDate dataRenovacao;

    @Column(length = 50)
    private String matricula;

    @Column(length = 50)
    private String habilitacao;

    @Enumerated(EnumType.STRING)
    private PilotoStatus status;

    @OneToMany(mappedBy = "piloto")
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

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public LocalDate getDataRenovacao() {
        return dataRenovacao;
    }

    public void setDataRenovacao(LocalDate dataRenovacao) {
        this.dataRenovacao = dataRenovacao;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getHabilitacao() {
        return habilitacao;
    }

    public void setHabilitacao(String habilitacao) {
        this.habilitacao = habilitacao;
    }

    public PilotoStatus getStatus() {
        return status;
    }

    public void setStatus(PilotoStatus status) {
        this.status = status;
    }

}
