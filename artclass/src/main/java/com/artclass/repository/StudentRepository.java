package com.artclass.repository;

import com.artclass.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    long countByActiveTrue();
    long countByActiveFalse();

    List<Student> findByActiveTrueOrderByNameAsc();
    List<Student> findAllByOrderByCreatedAtDesc();

    Optional<Student> findByStudentId(String studentId);

    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "LOWER(s.studentId) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           "s.phone LIKE CONCAT('%',:q,'%')")
    List<Student> search(@Param("q") String query);

    List<Student> findByClassTypeOrderByNameAsc(Student.ClassType classType);

    @Query("SELECT s FROM Student s WHERE s.feesComplete = false AND s.active = true")
    List<Student> findPendingFees();

    @Query("SELECT COUNT(s) FROM Student s WHERE s.feesComplete = false AND s.active = true")
    long countPendingFees();

    @Query("SELECT COALESCE(SUM(s.classFees + COALESCE(s.completionFees,0)), 0) FROM Student s WHERE s.active = true")
    Double totalRevenue();

    @Query("SELECT COALESCE(SUM(s.advanceFees), 0) FROM Student s WHERE s.active = true")
    Double totalCollected();
}
