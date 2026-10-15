package pmerida.localhost.carritogt.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pmerida.localhost.carritogt.dto.AgregarItemRequest;
import pmerida.localhost.carritogt.entity.ShoppingCart;
import pmerida.localhost.carritogt.service.CarritoService;

import java.util.List;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
public class CarritoController {

    private final CarritoService carritoService;

    @GetMapping("/{codUsuario}")
    public List<ShoppingCart> listar(@PathVariable("codUsuario") String codUsuario) {
        return carritoService.listar(codUsuario);
    }

    @PostMapping("/{codUsuario}")
    public ResponseEntity<ShoppingCart> agregar(@PathVariable("codUsuario") String codUsuario,
                                                @RequestBody AgregarItemRequest request) {
        ShoppingCart item = carritoService.agregar(codUsuario, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(item);
    }

    @DeleteMapping("/item/{idShoppingCart}")
    public ResponseEntity<Void> eliminar(@PathVariable("idShoppingCart") Integer idShoppingCart) {
        carritoService.eliminar(idShoppingCart);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{codUsuario}")
    public ResponseEntity<Void> vaciar(@PathVariable("codUsuario") String codUsuario) {
        carritoService.vaciar(codUsuario);
        return ResponseEntity.noContent().build();
    }
}