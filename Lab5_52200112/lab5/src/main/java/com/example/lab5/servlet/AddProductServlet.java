package com.example.lab5.servlet;

import com.example.lab5.dao.ProductDAO;
import com.example.lab5.model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/addProduct")
public class AddProductServlet extends HttpServlet {
    private final ProductDAO productDAO = new ProductDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Hiển thị form thêm sản phẩm
        request.getRequestDispatcher("product-management.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String productName = request.getParameter("name");
        String productPrice = request.getParameter("price");

        if (productName == null || productPrice == null || productName.isEmpty() || productPrice.isEmpty()) {
            request.setAttribute("error", "Vui lòng nhập đầy đủ thông tin!");
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }

        try {
            double price = Double.parseDouble(productPrice);
            if (price < 0) {
                request.setAttribute("error", "Giá sản phẩm không được âm!");
                request.getRequestDispatcher("index.jsp").forward(request, response);
                return;
            }

            Product product = new Product(productName, price);
            productDAO.saveProduct(product);
            response.sendRedirect(request.getContextPath() + "/product-management");
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Giá sản phẩm phải là một số hợp lệ!");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Có lỗi xảy ra khi thêm sản phẩm: " + e.getMessage());
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }
}