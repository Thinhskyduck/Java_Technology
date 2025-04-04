<%--
  Created by IntelliJ IDEA.
  User: ASUS
  Date: 3/28/2025
  Time: 4:47 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="jakarta.servlet.http.HttpSession" %>

<%
    if (session.getAttribute("user") != null) {
        response.sendRedirect("product-management");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Login Page</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</head>
<body>

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
            <h3 class="text-center text-secondary mt-5 mb-3">User Login</h3>
            <form class="border rounded w-100 mb-5 mx-auto px-3 pt-3 bg-light" action="${pageContext.request.contextPath}/login" method="post">
                <div class="form-group">
                    <label for="username">Username</label>
                    <input id="username" name="username" type="text" class="form-control" placeholder="Username">
                </div>
                <div class="form-group">
                    <label for="password">Password</label>
                    <input id="password" name="password" type="password" class="form-control" placeholder="Password">
                </div>
                <div class="form-group">
                    <input type="checkbox" id="remember" name="remember">
                    <label for="remember">Remember me</label>
                </div>
                <div class="form-group d-flex justify-content-between">  <%-- Sử dụng flex để đặt các nút cạnh nhau --%>
                    <button class="btn btn-success px-5" type="submit">Login</button>
                    <%-- Thêm nút Đăng ký --%>
                    <a href="${pageContext.request.contextPath}/register.jsp" class="btn btn-primary px-4">Register</a>
                </div>
                <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-danger mt-3"><%= request.getAttribute("error") %></div> <%-- Thêm margin-top để không dính vào nút --%>
                <% } %>
            </form>
        </div>
    </div>
</div>

</body>
</html>