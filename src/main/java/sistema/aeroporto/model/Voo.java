package sistema.aeroporto.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import sistema.aeroporto.model.enums.VooStatus;

@Entity
public class Voo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Piloto piloto;

    @ManyToOne
    private CompanhiaAerea companhia;

    @Column(length = 10)
    private String codigo;

    @Column(length = 4)
    private String origem;

    @Column(length = 4)
    private String destino;

    private LocalDateTime horarioPartidaPrevisto;
    private LocalDateTime horarioChegadaPrevisto;
    private LocalDateTime horarioPartidaReal;
    private LocalDateTime horarioChegadaReal;

    @Column(length = 255)
    private String motivoCancelamento;

    @Enumerated(EnumType.STRING)
    private VooStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Piloto getPiloto() {
        return piloto;
    }

    public void setPiloto(Piloto piloto) {
        this.piloto = piloto;
    }

    public CompanhiaAerea getCompanhia() {
        return companhia;
    }

    public void setCompanhia(CompanhiaAerea companhia) {
        this.companhia = companhia;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public LocalDateTime getHorarioPartidaPrevisto() {
        return horarioPartidaPrevisto;
    }

    public void setHorarioPartidaPrevisto(LocalDateTime horarioPartidaPrevisto) {
        this.horarioPartidaPrevisto = horarioPartidaPrevisto;
    }

    public LocalDateTime getHorarioChegadaPrevisto() {
        return horarioChegadaPrevisto;
    }

    public void setHorarioChegadaPrevisto(LocalDateTime horarioChegadaPrevisto) {
        this.horarioChegadaPrevisto = horarioChegadaPrevisto;
    }

    public LocalDateTime getHorarioPartidaReal() {
        return horarioPartidaReal;
    }

    public void setHorarioPartidaReal(LocalDateTime horarioPartidaReal) {
        this.horarioPartidaReal = horarioPartidaReal;
    }

    public LocalDateTime getHorarioChegadaReal() {
        return horarioChegadaReal;
    }

    public void setHorarioChegadaReal(LocalDateTime horarioChegadaReal) {
        this.horarioChegadaReal = horarioChegadaReal;
    }

    public VooStatus getStatus() {
        return status;
    }

    public void setStatus(VooStatus status) {
        this.status = status;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

}
