package com.example.lab5.dao;

import com.example.lab5.model.Product;
import com.example.lab5.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class ProductDAO {
    // Lấy danh sách tất cả sản phẩm
    public List<Product> getAllProducts() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Product ", Product.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Lưu sản phẩm mới vào cơ sở dữ liệu
    public void saveProduct(Product product) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(product);
            transaction.commit();
        }
    }

    // Xóa sản phẩm theo ID
    public void deleteProduct(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Product product = session.get(Product.class, id);
            if (product != null) {
                session.remove(product);
            }
            transaction.commit();
        }
    }

    // Lấy sản phẩm theo ID
    public Product getProductById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Product.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Cập nhật sản phẩm
    public boolean updateProduct(Product product) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Product existingProduct = session.get(Product.class, product.getId());
            if (existingProduct != null) {
                existingProduct.setName(product.getName());
                existingProduct.setPrice(product.getPrice());
                session.merge(existingProduct);
                transaction.commit();
                System.out.println("Cập nhật thành công ID: " + product.getId());
                return true;
            } else {
                transaction.rollback();
                System.out.println("Không tìm thấy sản phẩm ID: " + product.getId());
                return false;
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.out.println("Lỗi cập nhật ID: " + product.getId() + " - " + e.getMessage());
            return false;
        }
    }
}