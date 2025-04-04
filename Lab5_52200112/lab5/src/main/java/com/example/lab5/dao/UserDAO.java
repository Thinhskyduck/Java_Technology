package com.example.lab5.dao;

import com.example.lab5.model.User;
import com.example.lab5.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDAO {
    public void saveUser(User user) {
        Logger logger = LoggerFactory.getLogger(getClass()); // Lấy logger cho lớp hiện tại
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            logger.info("Bắt đầu lưu người dùng: {}", user); // In log trước khi lưu
            session.persist(user);
            transaction.commit();
            logger.info("Lưu người dùng thành công: {}", user); // In log sau khi lưu thành công
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
                logger.error("Lỗi khi lưu người dùng. Rollback transaction.", e); // In log lỗi và rollback
            } else {
                logger.error("Lỗi khi lưu người dùng. Không thể rollback transaction.", e); // In log lỗi không rollback được
            }

        }
    }

    public List<User> getAllUsers() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from User", User.class).list();
        }
    }

    public User getUserByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE username = :username", User.class);
            query.setParameter("username", username);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public User getUserByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE email = :email", User.class);
            query.setParameter("email", email);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}