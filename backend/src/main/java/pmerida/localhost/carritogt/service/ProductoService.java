package pmerida.localhost.carritogt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pmerida.localhost.carritogt.entity.Producto;
import pmerida.localhost.carritogt.repository.ProductoRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;

    public List<Producto> listar() {
        return productoRepository.listar();
    }
}