package com.example.ex2.model;

import jakarta.persistence.*; // Sử dụng jakarta.* cho Spring Boot 3+
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // Đánh dấu đây là một Entity
@Data // Lombok: Tự động tạo getter, setter, toString, equals, hashCode
@NoArgsConstructor // Lombok: Tạo constructor không tham số
@AllArgsConstructor // Lombok: Tạo constructor với tất cả tham số
public class Student {

    @Id // Đánh dấu là khóa chính
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tự động tăng ID
    private Long id;

    @Column(nullable = false, length = 100) // Cột 'name', không được null, độ dài tối đa 100
    private String name;

    @Column(nullable = false) // Cột 'age', không được null
    private int age;

    @Column(unique = true, nullable = false) // Cột 'email', không được null và phải là duy nhất
    private String email;

    @Column(name = "ielts_score") // (Optional) Đặt tên cột trong DB là ielts_score
    private double ieltsScore;

    // Lombok đã tạo constructors, getters, setters, toString,...

    // Bạn có thể tạo constructor tùy chỉnh nếu cần (ngoài cái AllArgsConstructor)
    // Ví dụ: Constructor không cần id (vì id tự tăng)
    public Student(String name, int age, String email, double ieltsScore) {
        this.name = name;
        this.age = age;
        this.email = email;
        this.ieltsScore = ieltsScore;
    }
}
