package com.example.lab5.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.example.lab5.dao.ProductDAO;
import com.example.lab5.model.Product;
import com.google.gson.Gson;

@WebServlet("/editProduct")
public class EditProductServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }

        int productId = Integer.parseInt(request.getParameter("id"));
        ProductDAO productDAO = new ProductDAO();
        Product product = productDAO.getProductById(productId);

        // Trả về dữ liệu sản phẩm dưới dạng JSON
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();
        if (product != null) {
            out.print(new Gson().toJson(product));
        } else {
            out.print(new Gson().toJson("Product not found"));
        }
        out.flush();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("user") == null) {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().print("{\"success\": false, \"message\": \"Please login\"}");
            return;
        }

        String idParam = request.getParameter("id");
        String nameParam = request.getParameter("name");
        String priceParam = request.getParameter("price");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (idParam == null || nameParam == null || priceParam == null || nameParam.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"success\": false, \"message\": \"Missing or invalid parameters\"}");
            return;
        }

        try {
            int productId = Integer.parseInt(idParam);
            double productPrice = Double.parseDouble(priceParam);

            Product product = new Product();
            product.setId(productId);
            product.setName(nameParam.trim());
            product.setPrice(productPrice);

            ProductDAO productDAO = new ProductDAO();
            boolean success = productDAO.updateProduct(product);

            PrintWriter out = response.getWriter();
            if (success) {
                out.print("{\"success\": true, \"message\": \"Product updated successfully\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print("{\"success\": false, \"message\": \"Update failed\"}");
            }
            out.flush();
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print("{\"success\": false, \"message\": \"Invalid number format\"}");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().print("{\"success\": false, \"message\": \"Server error\"}");
        }
    }
}