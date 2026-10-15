package pmerida.localhost.carritogt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import pmerida.localhost.carritogt.dto.AgregarItemRequest;
import pmerida.localhost.carritogt.entity.Producto;
import pmerida.localhost.carritogt.entity.ShoppingCart;
import pmerida.localhost.carritogt.repository.ProductoRepository;
import pmerida.localhost.carritogt.repository.ShoppingCartRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarritoService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductoRepository productoRepository;

    public List<ShoppingCart> listar(String codUsuario) {
        return shoppingCartRepository.listarPorUsuario(codUsuario);
    }

    @Transactional
    public ShoppingCart agregar(String codUsuario, AgregarItemRequest request) {
        if (request.getIdProducto() == null || request.getCantidad() == null || request.getCantidad() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "idProducto y cantidad (mínimo 1) son obligatorios");
        }

        Producto producto = productoRepository.buscarPorId(request.getIdProducto());
        if (producto == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El producto no existe");
        }

        ShoppingCart item = shoppingCartRepository
                .buscarPorUsuarioYProducto(codUsuario, producto.getIdProducto());

        if (item == null) {
            item = new ShoppingCart();
            item.setProducto(producto);
            item.setCodUsuario(codUsuario);
            item.setCantidad(request.getCantidad());
        } else {
            int actual = item.getCantidad() == null ? 0 : item.getCantidad();
            item.setCantidad(actual + request.getCantidad());
        }
        return shoppingCartRepository.guardar(item);
    }

    public void eliminar(Integer idShoppingCart) {
        if (shoppingCartRepository.eliminar(idShoppingCart) == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El item del carrito no existe");
        }
    }

    public void vaciar(String codUsuario) {
        shoppingCartRepository.vaciar(codUsuario);
    }
}