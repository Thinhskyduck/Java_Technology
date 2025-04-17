package com.example.ex6.repository; // Đảm bảo đúng package

import com.example.ex6.model.Student; // Đảm bảo đúng package model
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository; // Nên có @Repository

@Repository // Đánh dấu là một Spring bean Repository
public interface StudentPagingAndSortingRepository extends PagingAndSortingRepository<Student, Long> {
    // Không cần thêm phương thức nào ở đây cho yêu cầu hiện tại
    // Nó sẽ tự động kế thừa count(), findAll(Sort), findAll(Pageable), save(), findById(), v.v.
}