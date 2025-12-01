package sistema.aeroporto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import sistema.aeroporto.enums.vooStatus;
import sistema.aeroporto.model.Voo;

public interface VooRepository extends JpaRepository <Voo, Long> {
    
    // Método para buscar voos por destino
    List<Voo> findByDestino(String destino);

    // Método para buscar voos por origem
    List<Voo> findByOrigem(String origem);

    // Método para buscar voos por status
    List<Voo> findByStatus(vooStatus status);

    // Método para buscar voos por companhia aérea
    List<Voo> findByCompanhia_Id(Long idCompanhia);

    // Método para buscar voos por piloto
    List<Voo> findByPiloto_Id(Long idPiloto);

    // Método para verificar existência de código de voo
    boolean existsByCodigo(String codigo);
}
