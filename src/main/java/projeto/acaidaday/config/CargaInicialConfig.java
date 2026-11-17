package projeto.acaidaday.config;

import projeto.acaidaday.model.Adicional;
import projeto.acaidaday.model.Produto;
import projeto.acaidaday.model.Usuario;
import projeto.acaidaday.repository.AdicionalRepository;
import projeto.acaidaday.repository.ProdutoRepository;
import projeto.acaidaday.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class CargaInicialConfig {

    @Bean
    CommandLineRunner iniciarDados(UsuarioRepository usuarioRepo,
                                   ProdutoRepository produtoRepo,
                                   AdicionalRepository adicionalRepo,
                                   PasswordEncoder encoder) {
        return args -> {
            if (usuarioRepo.count() == 0) {
                Usuario admin = new Usuario();
                admin.setNome("Dayane (Admin)");
                admin.setEmail("admin@acaidaday.com");
                admin.setTelefone("84996038471");
                admin.setSenha(encoder.encode("123456"));
                admin.setPerfil("ADMIN");

                Usuario atendente = new Usuario();
                atendente.setNome("Atendente Loja");
                atendente.setEmail("atendente@acaidaday.com");
                atendente.setTelefone("84996038471");
                atendente.setSenha(encoder.encode("123456"));
                atendente.setPerfil("ATENDENTE");

                Usuario cliente = new Usuario();
                cliente.setNome("Cliente Teste");
                cliente.setEmail("cliente@acaidaday.com");
                cliente.setTelefone("84999990000");
                cliente.setSenha(encoder.encode("123456"));
                cliente.setPerfil("CLIENTE");

                usuarioRepo.saveAll(List.of(admin, atendente, cliente));
            }

            if (produtoRepo.count() == 0) {
                produtoRepo.saveAll(List.of(
                    criarProduto("Açaí Tradicional 200ml", "Direito a 3 adicionais/frutas grátis + até 2 cremes e 2 coberturas grátis", "200ml", "11.00"),
                    criarProduto("Açaí Tradicional 300ml", "Direito a 4 adicionais/frutas grátis + até 2 cremes e 2 coberturas grátis", "300ml", "14.00"),
                    criarProduto("Açaí Tradicional 500ml", "Direito a 5 adicionais/frutas grátis + até 2 cremes e 2 coberturas grátis", "500ml", "20.00"),
                    criarProduto("Açaí Tradicional 1 Litro", "Direito a 8 adicionais/frutas grátis + até 2 cremes e 2 coberturas grátis", "1000ml", "40.00"),
                    criarProduto("Açaí Proteico 300ml", "Açaí + Whey obrigatório + 4 adicionais/frutas grátis à escolha (sugestão: frutas, mel e granola)", "300ml", "16.00"),
                    criarProduto("Milkshake 400ml", "Escolha o sabor de Açaí ou qualquer um dos nossos 12 Cremes (selecione 1 creme abaixo ou deixe sem creme para Açaí)", "400ml", "15.00")
                ));
            }

            if (adicionalRepo.count() == 0) {
                adicionalRepo.saveAll(List.of(
                    // CREMES / SORVETES (Grátis - até 2 por açaí)
                    criarAdicional("[Creme] Choco Malte", "0.00"),
                    criarAdicional("[Creme] Cupuaçu", "0.00"),
                    criarAdicional("[Creme] Chiclete", "0.00"),
                    criarAdicional("[Creme] Guaraná", "0.00"),
                    criarAdicional("[Creme] Morango", "0.00"),
                    criarAdicional("[Creme] Morango Trufado", "0.00"),
                    criarAdicional("[Creme] Maracujá", "0.00"),
                    criarAdicional("[Creme] Ninho", "0.00"),
                    criarAdicional("[Creme] Ninho Trufado", "0.00"),
                    criarAdicional("[Creme] Oreo", "0.00"),
                    criarAdicional("[Creme] Tapioca", "0.00"),
                    criarAdicional("[Creme] Chocomenta", "0.00"),

                    // COBERTURAS (Grátis - até 2 por açaí)
                    criarAdicional("[Cobertura] Chocolate", "0.00"),
                    criarAdicional("[Cobertura] Caramelo", "0.00"),
                    criarAdicional("[Cobertura] Morango", "0.00"),
                    criarAdicional("[Cobertura] Menta", "0.00"),
                    criarAdicional("[Cobertura] Leite Condensado", "0.00"),

                    // EXTRAS PAGOS (Valor fixo por fora da cota)
                    criarAdicional("[Extra] Creme de Cookies (+R$ 2,00)", "2.00"),
                    criarAdicional("[Extra] Creme de Ninho Caseiro (+R$ 2,00)", "2.00"),
                    criarAdicional("[Extra] Cobertura de Nutella (+R$ 2,00)", "2.00"),
                    criarAdicional("[Extra] Creme Cravejado (+R$ 2,00)", "2.00"),
                    criarAdicional("[Extra] Creme de Pistache (+R$ 3,00)", "3.00"),

                    // FRUTAS (Contam na cota grátis; excedente = R$ 2,00)
                    criarAdicional("[Fruta] Abacaxi", "2.00"),
                    criarAdicional("[Fruta] Banana", "2.00"),
                    criarAdicional("[Fruta] Kiwi", "2.00"),
                    criarAdicional("[Fruta] Morango", "2.00"),
                    criarAdicional("[Fruta] Maçã", "2.00"),
                    criarAdicional("[Fruta] Uva", "2.00"),

                    // ADICIONAIS SECOS / COMPLEMENTOS (Contam na cota grátis; excedente = R$ 1,00)
                    criarAdicional("[Adicional] Amendoim", "1.00"),
                    criarAdicional("[Adicional] Baton Preto", "1.00"),
                    criarAdicional("[Adicional] Gotas de Chocolate Branco", "1.00"),
                    criarAdicional("[Adicional] Bis Preto", "1.00"),
                    criarAdicional("[Adicional] Bis Branco", "1.00"),
                    criarAdicional("[Adicional] Castanha", "1.00"),
                    criarAdicional("[Adicional] Chocobol / Chocopower", "1.00"),
                    criarAdicional("[Adicional] Coco Ralado", "1.00"),
                    criarAdicional("[Adicional] Coco Caramelizado na Castanha", "1.00"),
                    criarAdicional("[Adicional] Disquete", "1.00"),
                    criarAdicional("[Adicional] Farinha Láctea", "1.00"),
                    criarAdicional("[Adicional] Gotas de Chocolate", "1.00"),
                    criarAdicional("[Adicional] Granola", "1.00"),
                    criarAdicional("[Adicional] Jujuba", "1.00"),
                    criarAdicional("[Adicional] KitKat", "1.00"),
                    criarAdicional("[Adicional] Leite em Pó", "1.00"),
                    criarAdicional("[Adicional] Canudinho", "1.00"),
                    criarAdicional("[Adicional] Mini Oreo", "1.00"),
                    criarAdicional("[Adicional] Marshmallow", "1.00"),
                    criarAdicional("[Adicional] Ovomaltine", "1.00"),
                    criarAdicional("[Adicional] Paçoquinha", "1.00"),
                    criarAdicional("[Adicional] Ouro Branco", "1.00"),
                    criarAdicional("[Adicional] Cereja", "1.00"),
                    criarAdicional("[Adicional] Uvas Passas", "1.00"),
                    criarAdicional("[Adicional] Mel", "1.00")
                ));
            }
        };
    }

    private Produto criarProduto(String nome, String desc, String tamanho, String preco) {
        Produto p = new Produto();
        p.setNome(nome);
        p.setDescricao(desc);
        p.setTamanho(tamanho);
        p.setPreco(new BigDecimal(preco));
        p.setDisponivel(true);
        return p;
    }

    private Adicional criarAdicional(String nome, String preco) {
        Adicional a = new Adicional();
        a.setNome(nome);
        a.setPreco(new BigDecimal(preco));
        a.setDisponivel(true);
        return a;
    }
}