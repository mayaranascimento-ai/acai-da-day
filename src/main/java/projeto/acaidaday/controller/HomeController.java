package projeto.acaidaday.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projeto.acaidaday.model.Usuario;
import projeto.acaidaday.repository.*;

@Controller
public class HomeController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private AdicionalRepository adicionalRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String index() {
        return "redirect:/cardapio";
    }

    @GetMapping("/cardapio")
    public String cardapio(Model model) {
        model.addAttribute("produtos", produtoRepository.findByDisponivelTrue());
        model.addAttribute("adicionais", adicionalRepository.findByDisponivelTrue());
        return "cardapio";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/cadastro")
    public String formCadastro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String salvarCadastro(@ModelAttribute Usuario usuario, Model model) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            model.addAttribute("erro", "Este e-mail já está cadastrado!");
            return "cadastro";
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setPerfil("CLIENTE");
        usuarioRepository.save(usuario);
        return "redirect:/login?cadastrado";
    }

    @GetMapping("/redirecionar")
    public String redirecionarPorPerfil(Authentication auth) {
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isAtendente = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ATENDENTE"));

        if (isAdmin || isAtendente) {
            return "redirect:/painel/pedidos";
        }
        return "redirect:/cardapio";
    }
}