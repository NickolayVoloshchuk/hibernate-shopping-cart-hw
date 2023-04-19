package mate.academy.dao.impl;

import java.util.Optional;
import mate.academy.dao.ShoppingCartDao;
import mate.academy.exception.DataProcessingException;
import mate.academy.lib.Dao;
import mate.academy.model.ShoppingCart;
import mate.academy.model.User;
import mate.academy.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Dao
public class ShoppingCartDaoImpl implements ShoppingCartDao {
    @Override
    public ShoppingCart add(ShoppingCart shoppingCart) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.save(shoppingCart);
            transaction.commit();
            return shoppingCart;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can`t insert " + shoppingCart, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public Optional<ShoppingCart> getByUser(User user) {
        String query = "FROM ShoppingCart sc "
                + "LEFT JOIN FETCH sc.tickets t "
                + "LEFT JOIN FETCH sc.user "
                + "LEFT JOIN FETCH t.movieSession ms "
                + "LEFT JOIN FETCH ms.movie m "
                + "LEFT JOIN FETCH ms.cinemaHall ch "
                + "WHERE sc.user = :user";
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery(query, ShoppingCart.class)
                    .setParameter("user", user)
                    .uniqueResultOptional();
        } catch (Exception e) {
            throw new DataProcessingException("Can`t get shopping cart by " + user, e);
        }
    }

    @Override
    public void update(ShoppingCart shoppingCart) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.update(shoppingCart);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new DataProcessingException("Can`t update " + shoppingCart, e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}