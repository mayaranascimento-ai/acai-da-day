package projeto.acaidaday.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projeto.acaidaday.model.Produto;
import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByDisponivelTrue();
}