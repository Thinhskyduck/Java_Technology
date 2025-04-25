package com.example.lab9and10.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException; // Thêm import
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
// Thêm các import cho các lỗi khác nếu cần xử lý cụ thể
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.resource.NoResourceFoundException; // Cho 404 tốt hơn NoHandlerFound


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Xử lý lỗi validation (@Valid) - Giữ nguyên
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        // ... (code như cũ) ...
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        fieldError -> fieldError.getField(),
                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Giá trị không hợp lệ"
                ));
        body.put("errors", errors);
        body.put("message", "Validation thất bại");
        body.put("path", request.getDescription(false).replace("uri=", ""));
        logger.warn("Lỗi validation: {}", errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // Xử lý lỗi ResponseStatusException (ném từ controller/service) - Giữ nguyên
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Object> handleResponseStatusException(ResponseStatusException ex, WebRequest request) {
        // ... (code như cũ) ...
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", ex.getStatusCode().value());
        body.put("error", ((HttpStatus) ex.getStatusCode()).getReasonPhrase());
        body.put("message", ex.getReason());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        if (ex.getStatusCode().is4xxClientError()) {
            logger.warn("Lỗi phía client ({}): {}", ex.getStatusCode(), ex.getReason());
        } else {
            logger.error("Lỗi phía server ({}): {}", ex.getStatusCode(), ex.getReason(), ex.getCause());
        }
        return new ResponseEntity<>(body, ex.getStatusCode());
    }

    // THÊM: Xử lý lỗi Access Denied (403 Forbidden)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.FORBIDDEN.value());
        body.put("error", "Forbidden");
        body.put("message", "Từ chối truy cập: Bạn không có quyền truy cập tài nguyên này.");
        body.put("path", request.getDescription(false).replace("uri=", ""));

        logger.warn("Từ chối truy cập (403): {}", request.getDescription(false), ex); // Log cả exception để xem chi tiết nếu cần
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    // THÊM: Xử lý lỗi Method Not Supported (405 Method Not Allowed)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.METHOD_NOT_ALLOWED.value());
        body.put("error", "Method Not Allowed");
        body.put("message", "Phương thức HTTP '" + ex.getMethod() + "' không được hỗ trợ cho đường dẫn này. Các phương thức được hỗ trợ: " + ex.getSupportedHttpMethods());
        body.put("path", request.getDescription(false).replace("uri=", ""));

        logger.warn("Phương thức không được hỗ trợ (405): {} {}", ex.getMethod(), request.getDescription(false));
        return new ResponseEntity<>(body, HttpStatus.METHOD_NOT_ALLOWED);
    }

    // THÊM: Xử lý lỗi Not Found (404) cho tài nguyên không tìm thấy (thay thế NoHandlerFound)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "Not Found");
        body.put("message", "Không tìm thấy tài nguyên cho đường dẫn: " + ex.getResourcePath());
        body.put("path", request.getDescription(false).replace("uri=", "")); // Request path cũng có thể lấy từ request

        logger.warn("Không tìm thấy tài nguyên (404): {}", ex.getResourcePath());
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }


    // Handler dự phòng cho các Exception khác (500 Internal Server Error) - Giữ nguyên
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        // ... (code như cũ) ...
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Internal Server Error");
        body.put("message", "Đã xảy ra lỗi không mong muốn: " + ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        logger.error("Unhandled exception:", ex);
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}