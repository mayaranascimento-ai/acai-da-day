package projeto.acaidaday.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projeto.acaidaday.model.Usuario;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
}