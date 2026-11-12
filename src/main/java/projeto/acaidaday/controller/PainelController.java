package projeto.acaidaday.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projeto.acaidaday.model.*;
import projeto.acaidaday.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequestMapping("/painel")
public class PainelController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private FechamentoCaixaRepository fechamentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/pedidos")
    public String listarPedidos(Model model) {
        model.addAttribute("pedidos", pedidoRepository.findAllByOrderByDataHoraDesc());
        return "painel-pedidos";
    }

    @PostMapping("/pedidos/{id}/status")
    public String atualizarStatus(@PathVariable Long id, @RequestParam String status) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow();
        pedido.setStatus(status);
        pedidoRepository.save(pedido);
        return "redirect:/painel/pedidos";
    }

    @GetMapping("/fechamento")
    public String telaFechamento(Model model) {
        model.addAttribute("fechamentos", fechamentoRepository.findAllByOrderByDataFechamentoDesc());
        model.addAttribute("hoje", LocalDate.now());
        return "painel-fechamento";
    }

    @PostMapping("/fechamento")
    public String registrarFechamento(
            @RequestParam LocalDate dataFechamento,
            @RequestParam BigDecimal valorFinal,
            @RequestParam(required = false) String observacao,
            Authentication auth) {

        Usuario responsavel = usuarioRepository.findByEmail(auth.getName()).orElseThrow();

        FechamentoCaixa fc = new FechamentoCaixa();
        fc.setDataFechamento(dataFechamento);
        fc.setValorFinal(valorFinal);
        fc.setObservacao(observacao);
        fc.setResponsavel(responsavel);

        fechamentoRepository.save(fc);
        return "redirect:/painel/fechamento?sucesso";
    }
}