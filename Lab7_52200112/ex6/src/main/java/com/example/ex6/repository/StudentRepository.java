package com.example.ex6.repository;

import com.example.ex6.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param; // Import Param
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {

    /*
     * Các phương thức truy vấn cũ (dựa trên tên phương thức) đã được comment lại hoặc xóa đi.
     * Thay vào đó, chúng ta sử dụng @Query với JPQL (Java Persistence Query Language).
     */

    // 1. Danh sách sinh viên có tuổi >= x (tên phương thức ngắn gọn hơn)
    // Sử dụng JPQL: "SELECT s FROM Student s WHERE s.age >= :minAge"
    // :minAge là một named parameter, được liên kết với tham số phương thức 'age' qua @Param("minAge")
    @Query("SELECT s FROM Student s WHERE s.age >= :age")
    List<Student> findStudentsByMinAge(@Param("age") int age);

    // 2. Đếm số sinh viên có IELTS score = x (tên phương thức ngắn gọn hơn)
    // Sử dụng JPQL: "SELECT COUNT(s) FROM Student s WHERE s.ieltsScore = :score"
    // :score là named parameter, liên kết với tham số phương thức 'ieltsScore' qua @Param("score")
    @Query("SELECT COUNT(s) FROM Student s WHERE s.ieltsScore = :score")
    long countStudentsWithIeltsScore(@Param("score") double ieltsScore);

    // 3. Danh sách sinh viên có tên chứa từ xxx (không phân biệt hoa thường) (tên phương thức ngắn gọn hơn)
    // Sử dụng JPQL: "SELECT s FROM Student s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))"
    // - LOWER() để không phân biệt hoa thường.
    // - CONCAT('%', :keyword, '%') để tạo chuỗi tìm kiếm kiểu 'contains'.
    // - :keyword là named parameter, liên kết với tham số phương thức 'nameKeyword' qua @Param("keyword")
    @Query("SELECT s FROM Student s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Student> findStudentsByNameContaining(@Param("keyword") String nameKeyword);

    /*
    // Các phương thức cũ dựa trên tên (để tham khảo)
    // 1. Danh sách sinh viên có tuổi >= x
    List<Student> findByAgeGreaterThanEqual(int age);

    // 2. Đếm số sinh viên có IELTS score = x
    long countByIeltsScore(double ieltsScore);

    // 3. Danh sách sinh viên có tên chứa từ xxx (không phân biệt hoa thường)
    List<Student> findByNameContainingIgnoreCase(String keyword);
    */
}