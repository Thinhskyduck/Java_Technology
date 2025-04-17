package com.example.ex3.service;

import com.example.ex3.model.Student;

public interface StudentService {
    public Iterable<Student> getAllStudents();

    public Student getStudent(long id) throws Exception;

    public void  deleteStudent(long id);
    public Student save(Student student);


}
