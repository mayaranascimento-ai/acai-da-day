package projeto.acaidaday.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projeto.acaidaday.model.*;
import projeto.acaidaday.repository.*;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cliente")
public class ClientePedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private AdicionalRepository adicionalRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Número do WhatsApp da loja (DDD + Número, ex: 5584999999999)
    private static final String WHATSAPP_LOJA = "5584999999999";

    @PostMapping("/finalizar-pedido")
    public String finalizarPedido(
            @RequestParam String carrinhoJson,
            @RequestParam String enderecoEntrega,
            @RequestParam String formaPagamento,
            @RequestParam(required = false) String detalhesPagamento,
            Authentication auth) {

        Usuario cliente = usuarioRepository.findByEmail(auth.getName()).orElseThrow();
        JsonParser parser = JsonParserFactory.getJsonParser();
        List<Object> itensCarrinho = parser.parseList(carrinhoJson);

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setEnderecoEntrega(enderecoEntrega);
        pedido.setFormaPagamento(formaPagamento);
        pedido.setDetalhesPagamento(detalhesPagamento != null ? detalhesPagamento : "");

        BigDecimal totalPedido = BigDecimal.ZERO;

        for (Object obj : itensCarrinho) {
            @SuppressWarnings("unchecked")
            Map<String, Object> itemMap = (Map<String, Object>) obj;

            Long produtoId = Long.valueOf(itemMap.get("produtoId").toString());
            int quantidade = Integer.parseInt(itemMap.get("quantidade").toString());

            Produto produto = produtoRepository.findById(produtoId).orElseThrow();

            ItemPedido itemPedido = new ItemPedido();
            itemPedido.setPedido(pedido);
            itemPedido.setProduto(produto);
            itemPedido.setQuantidade(quantidade);
            itemPedido.setPrecoUnitario(produto.getPreco());

            BigDecimal subtotalItem = produto.getPreco();

            @SuppressWarnings("unchecked")
            List<Object> adicionaisIds = (List<Object>) itemMap.get("adicionaisIds");
            if (adicionaisIds != null) {
                for (Object adIdObj : adicionaisIds) {
                    Long adId = Long.valueOf(adIdObj.toString());
                    Adicional adicional = adicionalRepository.findById(adId).orElseThrow();

                    ItemPedidoAdicional ipa = new ItemPedidoAdicional();
                    ipa.setItemPedido(itemPedido);
                    ipa.setAdicional(adicional);
                    ipa.setPrecoAdicional(adicional.getPreco());
                    itemPedido.getAdicionais().add(ipa);

                    subtotalItem = subtotalItem.add(adicional.getPreco());
                }
            }

            totalPedido = totalPedido.add(subtotalItem.multiply(BigDecimal.valueOf(quantidade)));
            pedido.getItens().add(itemPedido);
        }

        pedido.setValorTotal(totalPedido);
        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        return "redirect:/cliente/pedido-confirmado/" + pedidoSalvo.getId();
    }

    @GetMapping("/pedido-confirmado/{id}")
    public String pedidoConfirmado(@PathVariable Long id, Authentication auth, Model model) {
        Pedido pedido = pedidoRepository.findById(id).orElseThrow();

        StringBuilder msg = new StringBuilder();
        msg.append("Olá *Açaí da Day*! Acabei de fazer o pedido *#").append(pedido.getId()).append("*\n\n");
        msg.append("*Cliente:* ").append(pedido.getCliente().getNome()).append("\n");
        msg.append("*Itens:*\n");

        for (ItemPedido item : pedido.getItens()) {
            msg.append("- ").append(item.getQuantidade()).append("x ")
               .append(item.getProduto().getNome()).append(" (").append(item.getProduto().getTamanho()).append(")");
            if (!item.getAdicionais().isEmpty()) {
                msg.append(" + Adicionais: ");
                for (int i = 0; i < item.getAdicionais().size(); i++) {
                    msg.append(item.getAdicionais().get(i).getAdicional().getNome());
                    if (i < item.getAdicionais().size() - 1) msg.append(", ");
                }
            }
            msg.append("\n");
        }

        msg.append("\n*Total:* R$ ").append(String.format("%.2f", pedido.getValorTotal())).append("\n");
        msg.append("*Pagamento:* ").append(pedido.getFormaPagamento());
        if (pedido.getDetalhesPagamento() != null && !pedido.getDetalhesPagamento().isBlank()) {
            msg.append(" (").append(pedido.getDetalhesPagamento()).append(")");
        }
        msg.append("\n*Entrega:* ").append(pedido.getEnderecoEntrega());

        String linkWhatsApp = "https://wa.me/" + WHATSAPP_LOJA + "?text=" +
                URLEncoder.encode(msg.toString(), StandardCharsets.UTF_8);

        model.addAttribute("pedido", pedido);
        model.addAttribute("linkWhatsApp", linkWhatsApp);
        return "pedido-confirmado";
    }

    @GetMapping("/meus-pedidos")
    public String meusPedidos(Authentication auth, Model model) {
        Usuario cliente = usuarioRepository.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("pedidos", pedidoRepository.findByClienteOrderByDataHoraDesc(cliente));
        return "meus-pedidos";
    }
}