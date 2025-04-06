<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
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
                        <td class="product-price">
                            <fmt:formatNumber value="${product.price}" type="number" groupingUsed="true"/>
                        </td>

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
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script> <%-- Nhúng jQuery nếu chưa có --%>
<script>
    $(document).ready(function() {
        // Bắt sự kiện click nút "Chỉnh sửa"
        $('.table').on('click', '.edit-product', function() {
            var $button = $(this);
            var $row = $button.closest('tr'); // Tìm hàng (tr) chứa nút được click
            var productId = $row.data('id'); // Lấy ID từ data-id của hàng

            // Lấy giá trị hiện tại từ các ô td
            var currentName = $row.find('.product-name').text();
            var currentPrice = $row.find('.product-price').text();

            // Thay thế td bằng input
            $row.find('.product-name').html('<input type="text" class="form-control form-control-sm edit-name" value="' + currentName + '">');
            $row.find('.product-price').html('<input type="number" class="form-control form-control-sm edit-price" value="' + currentPrice + '">');

            // Thay đổi nút "Chỉnh sửa" thành "Lưu" và thêm class để xử lý lưu
            $button.text('Lưu');
            $button.removeClass('btn-primary edit-product').addClass('btn-success save-product');
            // Vô hiệu hóa nút xóa tạm thời khi đang sửa
            $row.find('.btn-danger').hide();
        });

        // Bắt sự kiện click nút "Lưu"
        $('.table').on('click', '.save-product', function() {
            var $button = $(this);
            var $row = $button.closest('tr');
            var productId = $row.data('id');

            // Lấy giá trị mới từ input
            var newName = $row.find('.edit-name').val();
            var newPrice = $row.find('.edit-price').val();

            // --- Gửi AJAX POST đến Servlet ---
            $.ajax({
                url: '${pageContext.request.contextPath}/editProduct', // Đường dẫn tới servlet
                type: 'POST',
                data: {
                    id: productId,
                    name: newName,
                    price: newPrice
                },
                dataType: 'json', // Mong đợi nhận về JSON
                success: function(response) {
                    // --- Xử lý khi thành công ---
                    if (response.success) {
                        // Cập nhật lại nội dung td bằng giá trị mới
                        $row.find('.product-name').text(newName);
                        $row.find('.product-price').text(newPrice);

                        // Khôi phục nút "Lưu" thành "Chỉnh sửa"
                        $button.text('Chỉnh sửa');
                        $button.removeClass('btn-success save-product').addClass('btn-primary edit-product');
                        // Hiển thị lại nút xóa
                        $row.find('.btn-danger').show();
                    } else {
                        // Thông báo lỗi nếu server trả về success: false
                        alert('Lỗi cập nhật: ' + response.message);
                    }
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    // --- Xử lý khi có lỗi AJAX hoặc server trả về lỗi HTTP ---
                    console.error("AJAX Error:", textStatus, errorThrown);
                    // Cố gắng lấy thông báo lỗi từ response JSON nếu có
                    var errorMsg = 'Lỗi khi gửi yêu cầu cập nhật.';
                    try {
                        var errorResponse = JSON.parse(jqXHR.responseText);
                        if (errorResponse && errorResponse.message) {
                            errorMsg = errorResponse.message;
                        }
                    } catch(e) {
                        // Không parse được JSON
                    }
                    alert(errorMsg);
                    // Có thể bạn muốn khôi phục lại trạng thái ban đầu hoặc giữ nguyên input để sửa lỗi
                    // Ví dụ: Khôi phục nút
                    // $button.text('Chỉnh sửa');
                    // $button.removeClass('btn-success save-product').addClass('btn-primary edit-product');
                    // $row.find('.btn-danger').show();
                }
            });
        });
    });
</script>

</body>
</html>
