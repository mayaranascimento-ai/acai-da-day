package projeto.acaidaday.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projeto.acaidaday.model.Pedido;
import projeto.acaidaday.model.Usuario;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteOrderByDataHoraDesc(Usuario cliente);
    List<Pedido> findAllByOrderByDataHoraDesc();
}