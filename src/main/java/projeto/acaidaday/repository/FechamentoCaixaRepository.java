package projeto.acaidaday.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projeto.acaidaday.model.FechamentoCaixa;
import java.util.List;

public interface FechamentoCaixaRepository extends JpaRepository<FechamentoCaixa, Long> {
    List<FechamentoCaixa> findAllByOrderByDataFechamentoDesc();
}