package com.example.ex6.service;

import com.example.ex6.model.Student;

public interface StudentService {
    public Iterable<Student> getAllStudents();

    public Student getStudent(long id) throws Exception;

    public void  deleteStudent(long id);
    public Student save(Student student);


}
