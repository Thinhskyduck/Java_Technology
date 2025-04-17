package com.example.ex4.repository;

import com.example.ex4.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    // 1. Danh sách sinh viên có tuổi >= x
    List<Student> findByAgeGreaterThanEqual(int age);

    // 2. Đếm số sinh viên có IELTS score = x
    long countByIeltsScore(double ieltsScore);

    // 3. Danh sách sinh viên có tên chứa từ xxx (không phân biệt hoa thường)
    List<Student> findByNameContainingIgnoreCase(String keyword);
}
