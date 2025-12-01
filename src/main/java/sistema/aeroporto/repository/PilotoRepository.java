package sistema.aeroporto.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sistema.aeroporto.model.Piloto;

public interface PilotoRepository extends JpaRepository<Piloto, Long> {

    // Método para buscar piloto por CPF
    Optional<Piloto> findByCpf(String cpf);
    
    // Método para verificar existência de CPF
    boolean existsByCpf(String cpf);

    // Método para buscar piloto por matrícula
    Optional<Piloto> findByMatricula(String matricula);

    // Método para verificar existência de matrícula
    boolean existsByMatricula(String matricula);
}
