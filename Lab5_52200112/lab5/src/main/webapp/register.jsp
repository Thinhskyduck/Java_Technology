<%
    if (session.getAttribute("user") != null) {
        response.sendRedirect("product-management");
        return;
    }
%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>Register</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.2/css/all.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <style>
        p.item {
            padding: 16px;
            text-align: center;
            border-radius: 8px;
            color: white;
            background-color: green;
        }
    </style>
</head>

<body class="bg-secondary">

<h3 class="text-center my-5 text-light">Account Registration</h3>

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-10 col-lg-8 col-xl-5">
            <div class="border p-3 rounded bg-light">
                <!-- Form gửi dữ liệu đến RegisterServlet -->
                <form method="post" action="register">
                    <div class="form-group">
                        <label for="fullname">Fullname</label>
                        <div class="input-group mb-3">
                            <div class="input-group-prepend">
                                <span class="input-group-text">
                                    <i class="fas fa-user"></i>
                                </span>
                            </div>
                            <input id="fullname" name="fullname" type="text" class="form-control" placeholder="Your Name" required>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="email">Email</label>
                        <div class="input-group mb-3">
                            <div class="input-group-prepend">
                                <span class="input-group-text">
                                    <i class="fas fa-envelope"></i>
                                </span>
                            </div>
                            <input id="email" name="email" type="email" class="form-control" placeholder="Email" required>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="password">Password</label>
                        <div class="input-group mb-3">
                            <div class="input-group-prepend">
                                <span class="input-group-text">
                                    <i class="fas fa-lock"></i>
                                </span>
                            </div>
                            <input id="password" name="password" type="password" class="form-control" placeholder="Password" required>
                        </div>
                    </div>

                    <div class="form-group">
                        <label for="confirmPassword">Confirm Password</label>
                        <div class="input-group mb-3">
                            <div class="input-group-prepend">
                                <span class="input-group-text">
                                    <i class="fas fa-lock"></i>
                                </span>
                            </div>
                            <input id="confirmPassword" name="confirmPassword" type="password" class="form-control" placeholder="Confirm Password" required>
                        </div>
                    </div>

                    <!-- Hiển thị thông báo lỗi khi có lỗi -->
                    <c:if test="${not empty error}">
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <button type="button" class="close" data-dismiss="alert">&times;</button>
                                ${error}
                        </div>
                    </c:if>

                    <!-- Nút đăng ký -->
                    <div class="form-group">
                        <button class="btn btn-success px-5" type="submit">Register</button>
                    </div>

                    <div class="form-group">
                        <p>Already have an account? <a href="login.jsp">Login now!</a></p>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

</body>

</html>
