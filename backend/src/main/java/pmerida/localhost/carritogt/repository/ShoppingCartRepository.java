package pmerida.localhost.carritogt.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pmerida.localhost.carritogt.entity.ShoppingCart;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@Transactional
public class ShoppingCartRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<ShoppingCart> listarPorUsuario(String codUsuario) {
        return entityManager.createQuery(
                        "FROM ShoppingCart sc WHERE sc.codUsuario = :cod ORDER BY sc.idShoppingCart",
                        ShoppingCart.class)
                .setParameter("cod", codUsuario)
                .getResultList();
    }

    public ShoppingCart buscarPorUsuarioYProducto(String codUsuario, Integer idProducto) {
        List<ShoppingCart> lista = entityManager.createQuery(
                        "FROM ShoppingCart sc WHERE sc.codUsuario = :cod AND sc.producto.idProducto = :idp",
                        ShoppingCart.class)
                .setParameter("cod", codUsuario)
                .setParameter("idp", idProducto)
                .getResultList();
        return lista.isEmpty() ? null : lista.get(0);
    }

    public ShoppingCart guardar(ShoppingCart item) {
        if (item.getIdShoppingCart() == null) {
            entityManager.persist(item);
            return item;
        }
        return entityManager.merge(item);
    }

    public int eliminar(Integer idShoppingCart) {
        return entityManager.createQuery("DELETE FROM ShoppingCart sc WHERE sc.idShoppingCart = :id")
                .setParameter("id", idShoppingCart)
                .executeUpdate();
    }

    public int vaciar(String codUsuario) {
        return entityManager.createQuery("DELETE FROM ShoppingCart sc WHERE sc.codUsuario = :cod")
                .setParameter("cod", codUsuario)
                .executeUpdate();
    }
}