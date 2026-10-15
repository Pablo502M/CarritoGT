package pmerida.localhost.carritogt.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "shopping_cart")
@Getter
@Setter
public class ShoppingCart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idShoppingCart")
    private Integer idShoppingCart;

    @ManyToOne
    @JoinColumn(name = "idProducto")
    private Producto producto;

    @Column(name = "codUsuario")
    private String codUsuario;

    @Column(name = "cantidad")
    private Integer cantidad;

    @Transient
    public Double getSubTotal() {
        if (producto == null || producto.getPrecio() == null || cantidad == null) {
            return null;
        }
        return Math.round(producto.getPrecio() * cantidad * 100.0) / 100.0;
    }
}