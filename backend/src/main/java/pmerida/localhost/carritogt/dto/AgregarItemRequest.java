package pmerida.localhost.carritogt.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgregarItemRequest {
    private Integer idProducto;
    private Integer cantidad;
}