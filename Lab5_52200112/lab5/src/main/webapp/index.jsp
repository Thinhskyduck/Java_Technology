<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect("login");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>Danh sách sản phẩm</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.3/dist/umd/popper.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</head>
<body style="background-color: #f8f8f8">

<div class="container-fluid p-5">
    <div class="row mb-5">
        <div class="col-md-6">
            <h3>Product Management</h3>
        </div>
        <div class="col-md-6 text-right">
            Xin chào <span class="text-danger">${sessionScope.user.username}</span> | <a href="logout">Logout</a>
        </div>
    </div>

    <div class="row rounded border p-3">
        <div class="col-md-4">
            <h4 class="text-success">Thêm sản phẩm mới</h4>
            <form class="mt-3" method="post" action="${pageContext.request.contextPath}/addProduct">
                <div class="form-group">
                    <label for="product-name">Tên sản phẩm</label>
                    <input class="form-control" type="text" placeholder="Nhập tên sản phẩm" id="product-name" name="name">
                </div>
                <div class="form-group">
                    <label for="price">Giá sản phẩm</label>
                    <input class="form-control" type="number" placeholder="Nhập giá bán" id="price" name="price">
                </div>
                <div class="form-group">
                    <button class="btn btn-success mr-2">Thêm sản phẩm</button>
                </div>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>
            </form>
        </div>

        <div class="col-md-8">
            <h4 class="text-success">Danh sách sản phẩm</h4>
            <p>Chọn một sản phẩm cụ thể để chỉnh sửa trực tiếp</p>
            <table class="table table-striped">
                <thead>
                <tr>
                    <th>STT</th>
                    <th>Tên sản phẩm</th>
                    <th>Giá</th>
                    <th>Thao tác</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="product" items="${productList}">
                    <tr data-id="${product.id}">
                        <td class="product-id">${product.id}</td>
                        <td class="product-name">${product.name}</td>
                        <td class="product-price">${product.price}</td>
                        <td>
                            <button class="btn btn-primary edit-product">Chỉnh sửa</button>
                            <a href="deleteProduct?id=${product.id}" class="btn btn-danger" onclick="return confirm('Bạn có chắc chắn muốn xóa?')">Xóa</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script>
    $(document).ready(function() {
        $('.edit-product').on('click', function(e) {
            e.preventDefault();
            e.stopPropagation();

            var row = $(this).closest('tr');
            var productId = row.data('id');
            var productName = row.find('.product-name').text().trim();
            var productPrice = row.find('.product-price').text().trim();

            // Lưu giá trị gốc
            row.find('.product-name').data('original', productName);
            row.find('.product-price').data('original', productPrice);

            // Chuyển sang ô input
            row.find('.product-name').html('<input type="text" class="form-control edit-name" value="' + productName + '">');
            row.find('.product-price').html('<input type="number" class="form-control edit-price" value="' + productPrice + '">');

            $(this).text('Lưu')
                .removeClass('edit-product btn-primary')
                .addClass('save-product btn-success');
        });

        $(document).on('click', '.save-product', function(e) {
            e.preventDefault();
            e.stopPropagation();

            var row = $(this).closest('tr');
            var productId = row.data('id');
            var newName = row.find('.edit-name').val().trim();
            var newPrice = row.find('.edit-price').val();
            var originalName = row.find('.product-name').data('original');
            var originalPrice = row.find('.product-price').data('original');

            if (!newName || isNaN(newPrice) || newPrice < 0) {
                alert('Vui lòng nhập đầy đủ và đúng định dạng!');
                return;
            }

            console.log('Gửi AJAX:', { id: productId, name: newName, price: newPrice });

            $.ajax({
                url: '${pageContext.request.contextPath}/editProduct',
                type: 'POST',
                data: { id: productId, name: newName, price: newPrice },
                dataType: 'json',
                success: function(response) {
                    console.log('Phản hồi thành công:', response);
                    if (response && response.success) {
                        // Cập nhật thành công: hiển thị text mới
                        row.find('.product-name').text(newName);
                        row.find('.product-price').text(newPrice);
                    } else {
                        // Thất bại: quay lại giá trị gốc
                        row.find('.product-name').text(originalName);
                        row.find('.product-price').text(originalPrice);
                        alert('Lỗi: ' + (response && response.message ? response.message : 'Cập nhật thất bại'));
                    }
                    // Luôn chuyển nút về trạng thái ban đầu
                    row.find('.save-product').text('Chỉnh sửa')
                        .removeClass('save-product btn-success')
                        .addClass('edit-product btn-primary');
                },
                error: function(xhr, status, error) {
                    console.log('Lỗi AJAX:', xhr.status, status, error);
                    console.log('Phản hồi lỗi:', xhr.responseText);
                    // Lỗi: quay lại giá trị gốc
                    row.find('.product-name').text(originalName);
                    row.find('.product-price').text(originalPrice);
                    row.find('.save-product').text('Chỉnh sửa')
                        .removeClass('save-product btn-success')
                        .addClass('edit-product btn-primary');
                    alert('Lỗi kết nối: ' + (xhr.responseText || 'Không thể liên lạc với server'));
                }
            });
        });
    });
</script>

</body>
</html>
