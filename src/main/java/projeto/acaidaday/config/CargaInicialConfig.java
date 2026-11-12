package projeto.acaidaday.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import projeto.acaidaday.model.*;
import projeto.acaidaday.repository.*;
import java.math.BigDecimal;

@Configuration
public class CargaInicialConfig {

    @Bean
    public CommandLineRunner carregarDados(
            UsuarioRepository usuarioRepo,
            ProdutoRepository produtoRepo,
            AdicionalRepository adicionalRepo,
            PasswordEncoder encoder) {
        return args -> {
            if (usuarioRepo.count() == 0) {
                Usuario admin = new Usuario();
                admin.setNome("Dayane (Admin)");
                admin.setEmail("admin@acaidaday.com");
                admin.setSenha(encoder.encode("123456"));
                admin.setTelefone("84999990001");
                admin.setPerfil("ADMIN");
                usuarioRepo.save(admin);

                Usuario atendente = new Usuario();
                atendente.setNome("Atendente Loja");
                atendente.setEmail("atendente@acaidaday.com");
                atendente.setSenha(encoder.encode("123456"));
                atendente.setTelefone("84999990002");
                atendente.setPerfil("ATENDENTE");
                usuarioRepo.save(atendente);

                Usuario cliente = new Usuario();
                cliente.setNome("Cliente Teste");
                cliente.setEmail("cliente@acaidaday.com");
                cliente.setSenha(encoder.encode("123456"));
                cliente.setTelefone("84999990003");
                cliente.setPerfil("CLIENTE");
                usuarioRepo.save(cliente);
            }

            if (produtoRepo.count() == 0) {
                Produto p1 = new Produto();
                p1.setNome("Açaí Tradicional");
                p1.setTamanho("300ml");
                p1.setDescricao("Açaí cremoso batido na hora");
                p1.setPreco(new BigDecimal("12.00"));
                produtoRepo.save(p1);

                Produto p2 = new Produto();
                p2.setNome("Açaí Tradicional");
                p2.setTamanho("500ml");
                p2.setDescricao("Açaí cremoso batido na hora");
                p2.setPreco(new BigDecimal("16.00"));
                produtoRepo.save(p2);

                Produto p3 = new Produto();
                p3.setNome("Açaí no Copo");
                p3.setTamanho("700ml");
                p3.setDescricao("Ideal para matar aquela fome");
                p3.setPreco(new BigDecimal("22.00"));
                produtoRepo.save(p3);
            }

            if (adicionalRepo.count() == 0) {
                Adicional a1 = new Adicional();
                a1.setNome("Leite Condensado");
                a1.setPreco(new BigDecimal("2.00"));
                adicionalRepo.save(a1);

                Adicional a2 = new Adicional();
                a2.setNome("Leite em Pó");
                a2.setPreco(new BigDecimal("2.50"));
                adicionalRepo.save(a2);

                Adicional a3 = new Adicional();
                a3.setNome("Granola");
                a3.setPreco(new BigDecimal("1.50"));
                adicionalRepo.save(a3);

                Adicional a4 = new Adicional();
                a4.setNome("Paçoca");
                a4.setPreco(new BigDecimal("2.00"));
                adicionalRepo.save(a4);
            }
        };
    }
}