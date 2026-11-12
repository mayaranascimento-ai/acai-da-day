package projeto.acaidaday.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class ItemPedidoAdicional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_pedido_id", nullable = false)
    private ItemPedido itemPedido;

    @ManyToOne
    @JoinColumn(name = "adicional_id", nullable = false)
    private Adicional adicional;

    @Column(nullable = false)
    private BigDecimal precoAdicional;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ItemPedido getItemPedido() { return itemPedido; }
    public void setItemPedido(ItemPedido itemPedido) { this.itemPedido = itemPedido; }
    public Adicional getAdicional() { return adicional; }
    public void setAdicional(Adicional adicional) { this.adicional = adicional; }
    public BigDecimal getPrecoAdicional() { return precoAdicional; }
    public void setPrecoAdicional(BigDecimal precoAdicional) { this.precoAdicional = precoAdicional; }
}