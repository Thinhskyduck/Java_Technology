package com.example.lab8;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

@Controller
public class HomeController {

    // Trang chủ - chỉ GET
    @GetMapping("/")
    public String home() {
        return "index";
    }

    // Trang /about - chỉ GET, trả về văn bản
    @GetMapping("/about")
    @ResponseBody
    public String about() {
        return "About this site";
    }

    // Form liên hệ - GET
    @GetMapping("/contact")
    public String contactForm(Model model) {
        model.addAttribute("userInfo", new UserInfo());
        return "contact";
    }

    // Gửi thông tin liên hệ - POST
    @PostMapping("/contact")
    public String submitContact(@ModelAttribute UserInfo userInfo, Model model) {
        model.addAttribute("userInfo", userInfo);
        return "result";
    }

    // Xử lý lỗi method không hỗ trợ
    @ExceptionHandler
    public String handleMethodNotAllowed(Exception e, Model model, HttpServletRequest request) {
        if (e instanceof org.springframework.web.HttpRequestMethodNotSupportedException) {
            model.addAttribute("errorMessage", "Method " + request.getMethod() + " không được hỗ trợ tại URL này.");
            return "error";
        }
        return "error";
    }

    // Xử lý lỗi không tìm thấy trang
    @ExceptionHandler(NoHandlerFoundException.class)
    public String handleNotFound(HttpServletRequest request, Model model) {
        model.addAttribute("errorMessage", "Không tìm thấy trang: " + request.getRequestURI());
        return "error";
    }

}
