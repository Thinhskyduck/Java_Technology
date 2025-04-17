package com.example.ex2.service;

import com.example.ex2.model.Student;

public interface StudentService {
    public Iterable<Student> getAllStudents();

    public Student getStudent(long id) throws Exception;

    public void  deleteStudent(long id);
    public Student save(Student student);


}
