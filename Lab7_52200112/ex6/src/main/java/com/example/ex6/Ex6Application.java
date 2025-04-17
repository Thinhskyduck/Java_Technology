package com.example.ex6;

import com.example.ex6.model.Student;
import com.example.ex6.repository.StudentPagingAndSortingRepository; // Import repository mới
import com.example.ex6.repository.StudentRepository; // Vẫn giữ nếu bạn muốn dùng cả hai
import com.example.ex6.service.StudentService; // Service vẫn dùng StudentRepository cũ hoặc có thể cập nhật nếu cần
import org.slf4j.Logger; // Sử dụng Logger thay vì System.out
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page; // Import Page
import org.springframework.data.domain.PageRequest; // Import PageRequest
import org.springframework.data.domain.Pageable; // Import Pageable
import org.springframework.data.domain.Sort; // Import Sort

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class Ex6Application implements CommandLineRunner { // Có thể đổi tên thành Ex6Application nếu muốn

	private static final Logger log = LoggerFactory.getLogger(Ex6Application.class);

	// Giữ lại Service nếu vẫn cần các thao tác CRUD cơ bản
	@Autowired
	private StudentService studentService; // Service này đang dùng StudentRepository

	// Inject Repository mới cho chức năng Paging & Sorting
	@Autowired
	private StudentPagingAndSortingRepository studentPagingRepo;

	// Bạn cũng có thể inject StudentRepository cũ nếu cần truy cập các phương thức @Query cũ
	@Autowired
	private StudentRepository studentRepo;

	public static void main(String[] args) {
		SpringApplication.run(Ex6Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("=== Bắt đầu thêm dữ liệu sinh viên ===");

		// Thêm nhiều sinh viên hơn để kiểm tra paging và sorting
		List<Student> studentsToAdd = Arrays.asList(
				new Student("Nguyen Van An", 20, "an@example.com", 6.5),
				new Student("Tran Thi Bich", 22, "bich@example.com", 7.0),
				new Student("Le Van Cu", 21, "cu@example.com", 6.0),
				new Student("Pham Thi Dung", 20, "dung@example.com", 7.5), // Cùng tuổi với An
				new Student("Hoang Van E", 23, "e@example.com", 8.0),
				new Student("Do Thi G", 22, "g@example.com", 6.5),     // Cùng tuổi với Bich
				new Student("Trinh Van H", 19, "h@example.com", 5.5),
				new Student("Vu Thi K", 20, "k@example.com", 8.0),     // Cùng tuổi với An, Dung
				new Student("Mai Van L", 24, "l@example.com", 7.0),
				new Student("Bui Thi M", 21, "m@example.com", 6.8),     // Cùng tuổi với Cu
				new Student("Duong Van N", 22, "n@example.com", 7.2)      // Cùng tuổi với Bich, G
		);

		studentsToAdd.forEach(studentService::save); // Dùng service để lưu

//		log.info("=== Dữ liệu sau khi thêm: {} sinh viên ===", studentPagingRepo.count());

		// --- Yêu cầu 1: Sắp xếp ---
		log.info("=== Danh sách sinh viên sắp xếp theo tuổi giảm dần, điểm IELTS tăng dần ===");
		// Tạo đối tượng Sort: Sắp xếp theo 'age' giảm dần (descending), sau đó theo 'ieltsScore' tăng dần (ascending)
		Sort sortCriteria = Sort.by(Sort.Order.desc("age"), Sort.Order.asc("ieltsScore"));
		// Gọi phương thức findAll với Sort
		Iterable<Student> sortedStudents = studentPagingRepo.findAll(sortCriteria);
		sortedStudents.forEach(student -> log.info(student.toString()));


		// --- Yêu cầu 2: Phân trang ---
		log.info("=== Lấy sinh viên thứ 4, 5, 6 (Trang 1, kích thước 3) ===");
		// Sinh viên 1, 2, 3 là trang 0
		// Sinh viên 4, 5, 6 là trang 1
		// Page number là 0-based index, size là số lượng phần tử mỗi trang.
		int pageNumber = 1; // Trang thứ 2 (index 1) để lấy SV 4, 5, 6
		int pageSize = 3;   // Mỗi trang 3 SV
		Pageable pageable = PageRequest.of(pageNumber, pageSize);

		// Gọi phương thức findAll với Pageable
		Page<Student> studentPage = studentPagingRepo.findAll(pageable);

		log.info("Thông tin trang:");
		log.info(" - Tổng số phần tử: {}", studentPage.getTotalElements());
		log.info(" - Tổng số trang: {}", studentPage.getTotalPages());
		log.info(" - Số trang hiện tại (0-based): {}", studentPage.getNumber());
		log.info(" - Số phần tử trên trang hiện tại: {}", studentPage.getNumberOfElements());
		log.info(" - Kích thước trang: {}", studentPage.getSize());

		log.info("Danh sách sinh viên trên trang {}: ", pageNumber);
		List<Student> studentsOnPage = studentPage.getContent(); // Lấy danh sách SV từ đối tượng Page
		if (studentsOnPage.isEmpty()) {
			log.warn("Không tìm thấy sinh viên nào ở trang {} với kích thước trang {}", pageNumber, pageSize);
		} else {
			studentsOnPage.forEach(student -> log.info(student.toString()));
		}

		// (Optional) Bạn có thể giữ lại hoặc xóa các phần update/delete từ Ex5 nếu muốn
		// log.info("=== Thử cập nhật và xoá (ví dụ) ===");
		// Student studentToUpdate = studentService.getStudent(2L); // Giả sử ID 2 tồn tại
		// if (studentToUpdate != null) {
		//     studentToUpdate.setName("TRAN THI BICH UPDATED");
		//     studentService.save(studentToUpdate);
		//     log.info("Đã cập nhật sinh viên ID 2");
		// }
		// studentService.deleteStudent(1L); // Giả sử ID 1 tồn tại
		// log.info("Đã xoá sinh viên ID 1");
		// log.info("=== Số sinh viên còn lại: {} ===", studentPagingRepo.count());

	}
}