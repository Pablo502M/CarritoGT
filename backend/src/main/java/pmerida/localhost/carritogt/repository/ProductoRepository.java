package pmerida.localhost.carritogt.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pmerida.localhost.carritogt.entity.Producto;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@Transactional
public class ProductoRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Producto> listar() {
        return entityManager.createQuery("FROM Producto", Producto.class).getResultList();
    }

    public Producto buscarPorId(Integer idProducto) {
        return entityManager.find(Producto.class, idProducto);
    }
}