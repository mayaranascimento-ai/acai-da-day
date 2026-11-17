package projeto.acaidaday.controller;

import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import projeto.acaidaday.model.*;
import projeto.acaidaday.repository.*;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cliente")
public class ClientePedidoController {

    private final PedidoRepository pedidoRepo;
    private final ProdutoRepository produtoRepo;
    private final AdicionalRepository adicionalRepo;
    private final UsuarioRepository usuarioRepo;
    private final JsonParser jsonParser = new BasicJsonParser();

    // WhatsApp Oficial Açaí da Day
    private static final String WHATSAPP_LOJA = "5584996038471";

    public ClientePedidoController(PedidoRepository pedidoRepo,
                                   ProdutoRepository produtoRepo,
                                   AdicionalRepository adicionalRepo,
                                   UsuarioRepository usuarioRepo) {
        this.pedidoRepo = pedidoRepo;
        this.produtoRepo = produtoRepo;
        this.adicionalRepo = adicionalRepo;
        this.usuarioRepo = usuarioRepo;
    }

    @GetMapping("/meus-pedidos")
    public String meusPedidos(Authentication auth, Model model) {
        Usuario cliente = usuarioRepo.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("pedidos", pedidoRepo.findByClienteOrderByDataHoraDesc(cliente));
        return "cliente/meus-pedidos";
    }

    @PostMapping("/finalizar-pedido")
    @SuppressWarnings("unchecked")
    public String finalizarPedido(Authentication auth,
                                  @RequestParam String carrinhoJson,
                                  @RequestParam String formaPagamento,
                                  @RequestParam(required = false) String detalhesPagamento,
                                  @RequestParam String enderecoEntrega) {

        Usuario cliente = usuarioRepo.findByEmail(auth.getName()).orElseThrow();
        List<Object> itensCarrinho = jsonParser.parseList(carrinhoJson);

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setDataHora(LocalDateTime.now());
        pedido.setStatus("PENDENTE");
        pedido.setFormaPagamento(formaPagamento);
        pedido.setDetalhesPagamento(detalhesPagamento);
        pedido.setEnderecoEntrega(enderecoEntrega);

        List<ItemPedido> itensPedido = new ArrayList<>();
        BigDecimal totalGeral = BigDecimal.ZERO;

        StringBuilder msg = new StringBuilder();
        msg.append("Olá *Açaí da Day*! Acabei de fazer um pedido pelo site:\n\n");
        msg.append("*Cliente:* ").append(cliente.getNome()).append("\n");
        msg.append("*Itens do Pedido:*\n");

        for (Object itemObj : itensCarrinho) {
            Map<String, Object> itemMap = (Map<String, Object>) itemObj;
            Long produtoId = Long.valueOf(itemMap.get("produtoId").toString());
            int quantidade = Integer.parseInt(itemMap.get("quantidade").toString());

            Produto produto = produtoRepo.findById(produtoId).orElseThrow();

            ItemPedido item = new ItemPedido();
            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(quantidade);
            item.setPrecoUnitario(produto.getPreco());

            int cotaGratis = obterCotaAdicionaisGratis(produto);
            BigDecimal somaExtrasUnitario = BigDecimal.ZERO;
            List<ItemPedidoAdicional> listaItemAdicionais = new ArrayList<>();

            Object rawIds = itemMap.get("adicionaisIds");
            if (rawIds instanceof List<?> idsList && !idsList.isEmpty()) {
                List<Long> ids = new ArrayList<>();
                for (Object idObj : idsList) {
                    ids.add(Long.valueOf(idObj.toString()));
                }

                List<Adicional> selecionados = adicionalRepo.findAllById(ids);
                selecionados.sort(Comparator.comparing(Adicional::getPreco).reversed());

                int usadosNaCota = 0;
                for (Adicional ad : selecionados) {
                    String nome = ad.getNome();
                    BigDecimal valorCobrado = BigDecimal.ZERO;

                    if (nome.startsWith("[Creme]") || nome.startsWith("[Cobertura]")) {
                        valorCobrado = BigDecimal.ZERO;
                    } else if (nome.startsWith("[Extra]")) {
                        valorCobrado = ad.getPreco();
                    } else if (nome.startsWith("[Fruta]") || nome.startsWith("[Adicional]")) {
                        if (usadosNaCota < cotaGratis) {
                            valorCobrado = BigDecimal.ZERO;
                            usadosNaCota++;
                        } else {
                            valorCobrado = ad.getPreco();
                        }
                    } else {
                        valorCobrado = ad.getPreco();
                    }

                    ItemPedidoAdicional ipa = new ItemPedidoAdicional();
                    ipa.setItemPedido(item);
                    ipa.setAdicional(ad);
                    ipa.setPrecoAdicional(valorCobrado);
                    listaItemAdicionais.add(ipa);

                    somaExtrasUnitario = somaExtrasUnitario.add(valorCobrado);
                }
            }

            item.setAdicionais(listaItemAdicionais);
            itensPedido.add(item);

            BigDecimal subtotalItem = produto.getPreco().add(somaExtrasUnitario)
                    .multiply(BigDecimal.valueOf(quantidade));
            totalGeral = totalGeral.add(subtotalItem);

            msg.append("• *").append(quantidade).append("x ").append(produto.getNome())
               .append("* (R$ ").append(subtotalItem).append(")\n");

            if (produto.getNome().toLowerCase().contains("proteico")) {
                msg.append("   _Inclui Whey Protein obrigatório_\n");
            }

            if (!listaItemAdicionais.isEmpty()) {
                msg.append("   _Escolhas:_ ");
                for (int i = 0; i < listaItemAdicionais.size(); i++) {
                    ItemPedidoAdicional ipa = listaItemAdicionais.get(i);
                    msg.append(ipa.getAdicional().getNome());
                    if (ipa.getPrecoAdicional().compareTo(BigDecimal.ZERO) > 0) {
                        msg.append(" (+R$ ").append(ipa.getPrecoAdicional()).append(")");
                    }
                    if (i < listaItemAdicionais.size() - 1) msg.append(", ");
                }
                msg.append("\n");
            }
        }

        pedido.setItens(itensPedido);
        pedido.setValorTotal(totalGeral);
        Pedido salvo = pedidoRepo.save(pedido);

        msg.insert(msg.indexOf("\n\n"), " (Pedido *#" + salvo.getId() + "*)");
        msg.append("\n*Total:* R$ ").append(totalGeral).append("\n");
        msg.append("*Pagamento:* ").append(formaPagamento);
        if (detalhesPagamento != null && !detalhesPagamento.isBlank()) {
            msg.append(" (").append(detalhesPagamento).append(")");
        }
        msg.append("\n*Entrega:* ").append(enderecoEntrega);

        String urlWhats = "https://wa.me/" + WHATSAPP_LOJA + "?text=" +
                URLEncoder.encode(msg.toString(), StandardCharsets.UTF_8);

        return "redirect:" + urlWhats;
    }

    private int obterCotaAdicionaisGratis(Produto produto) {
        String nome = produto.getNome().toLowerCase();
        String tam = produto.getTamanho() != null ? produto.getTamanho().toLowerCase() : "";
        if (nome.contains("milkshake")) return 0;
        if (tam.contains("200")) return 3;
        if (tam.contains("300")) return 4;
        if (tam.contains("500")) return 5;
        if (tam.contains("1000") || nome.contains("1 litro")) return 8;
        return 0;
    }
}