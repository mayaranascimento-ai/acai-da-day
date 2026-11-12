package projeto.acaidaday.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projeto.acaidaday.model.*;
import projeto.acaidaday.repository.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private AdicionalRepository adicionalRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/produtos")
    public String gerenciarProdutos(Model model) {
        model.addAttribute("produtos", produtoRepository.findAll());
        model.addAttribute("adicionais", adicionalRepository.findAll());
        return "admin-produtos";
    }

    @PostMapping("/produtos/salvar")
    public String salvarProduto(
            @RequestParam String nome,
            @RequestParam String tamanho,
            @RequestParam String descricao,
            @RequestParam BigDecimal preco) {

        Produto p = new Produto();
        p.setNome(nome);
        p.setTamanho(tamanho);
        p.setDescricao(descricao);
        p.setPreco(preco);
        p.setDisponivel(true);
        produtoRepository.save(p);
        return "redirect:/admin/produtos";
    }

    @PostMapping("/produtos/{id}/alternar")
    public String alternarDisponibilidadeProduto(@PathVariable Long id) {
        Produto p = produtoRepository.findById(id).orElseThrow();
        p.setDisponivel(!p.isDisponivel());
        produtoRepository.save(p);
        return "redirect:/admin/produtos";
    }

    @PostMapping("/adicionais/salvar")
    public String salvarAdicional(@RequestParam String nome, @RequestParam BigDecimal preco) {
        Adicional a = new Adicional();
        a.setNome(nome);
        a.setPreco(preco);
        a.setDisponivel(true);
        adicionalRepository.save(a);
        return "redirect:/admin/produtos";
    }

    @PostMapping("/adicionais/{id}/alternar")
    public String alternarDisponibilidadeAdicional(@PathVariable Long id) {
        Adicional a = adicionalRepository.findById(id).orElseThrow();
        a.setDisponivel(!a.isDisponivel());
        adicionalRepository.save(a);
        return "redirect:/admin/produtos";
    }

    @GetMapping("/usuarios")
    public String gerenciarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "admin-usuarios";
    }

    @PostMapping("/usuarios/{id}/perfil")
    public String alterarPerfil(@PathVariable Long id, @RequestParam String perfil) {
        Usuario u = usuarioRepository.findById(id).orElseThrow();
        u.setPerfil(perfil);
        usuarioRepository.save(u);
        return "redirect:/admin/usuarios";
    }
}