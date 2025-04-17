package com.example.ex2;

import com.example.ex2.model.Student;
import com.example.ex2.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Ex2Application implements CommandLineRunner {

	@Autowired
	private StudentService studentService;

	public static void main(String[] args) {
		SpringApplication.run(Ex2Application.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Thêm ít nhất 3 sinh viên vào database
		Student s1 = new Student("Nguyen Van A", 20, "a@example.com", 6.5);
		Student s2 = new Student("Tran Thi B", 22, "b@example.com", 7.0);
		Student s3 = new Student("Le Van C", 21, "c@example.com", 6.0);

		studentService.save(s1);
		studentService.save(s2);
		studentService.save(s3);

		System.out.println("=== Danh sách sinh viên sau khi thêm ===");
		studentService.getAllStudents().forEach(System.out::println);

		// Cập nhật thông tin của một sinh viên (ví dụ s2)
		s2.setName("Tran Thi B Updated");
		s2.setIeltsScore(7.5);
		studentService.save(s2);

		System.out.println("=== Danh sách sinh viên sau khi cập nhật ===");
		studentService.getAllStudents().forEach(System.out::println);

		// Xoá một sinh viên (ví dụ s1)
		studentService.deleteStudent(s1.getId());

		System.out.println("=== Danh sách sinh viên sau khi xoá ===");
		studentService.getAllStudents().forEach(System.out::println);
	}
}
