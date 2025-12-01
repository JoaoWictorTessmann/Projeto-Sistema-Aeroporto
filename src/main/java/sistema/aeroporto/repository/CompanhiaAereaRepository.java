package sistema.aeroporto.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sistema.aeroporto.model.CompanhiaAerea;

public interface CompanhiaAereaRepository extends JpaRepository<CompanhiaAerea, Long> {

    // Verifica se já existe uma companhia com o mesmo nome
    boolean existsByNome(String nome);

    // Verifica se já existe uma companhia com o mesmo CNPJ
    boolean existsByCnpj(String cnpj);

    // Busca por nome
    Optional<CompanhiaAerea> findByNome(String nome);

    // Busca por CNPJ
    Optional<CompanhiaAerea> findByCnpj(String cnpj);
}