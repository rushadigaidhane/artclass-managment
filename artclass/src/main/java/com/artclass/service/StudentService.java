package com.artclass.service;

import com.artclass.model.Student;
import com.artclass.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository repo;

    public List<Student> getAll() {
        return repo.findAllByOrderByCreatedAtDesc();
    }

    public Optional<Student> getById(Long id) {
        return repo.findById(id);
    }

    public Student save(Student student) {
        if (student.getId() == null) {
            // Auto-generate student ID
            String sid = generateStudentId(student.getClassType());
            student.setStudentId(sid);
        }
        return repo.save(student);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public void toggleStatus(Long id) {
        repo.findById(id).ifPresent(s -> {
            s.setActive(!s.getActive());
            repo.save(s);
        });
    }

    public void markFeesComplete(Long id, boolean complete) {
        repo.findById(id).ifPresent(s -> {
            s.setFeesComplete(complete);
            repo.save(s);
        });
    }

    public List<Student> search(String q) {
        return repo.search(q);
    }

    public List<Student> getByClass(Student.ClassType type) {
        return repo.findByClassTypeOrderByNameAsc(type);
    }

    public List<Student> getPendingFees() {
        return repo.findPendingFees();
    }

    // Stats
    public long totalStudents()   { return repo.count(); }
    public long activeStudents()  { return repo.countByActiveTrue(); }
    public long inactiveStudents(){ return repo.countByActiveFalse(); }
    public long pendingFees()     { return repo.countPendingFees(); }

    public Double totalRevenue()   {
        Double v = repo.totalRevenue();
        return v != null ? v : 0.0;
    }
    public Double totalCollected() {
        Double v = repo.totalCollected();
        return v != null ? v : 0.0;
    }

    // Generate ID like ART-001, BFA-023 etc.
    private String generateStudentId(Student.ClassType type) {
        String prefix = switch (type) {
            case ELEMENTARY   -> "ELM";
            case INTERMEDIATE -> "INT";
            case DRAWING      -> "DRW";
            case SKETCHING    -> "SKT";
            case MEHENDI      -> "MHN";
            case RANGOLI      -> "RNG";
            case CRAFT        -> "CRF";
            case BFA_ENTRANCE -> "BFA";
            case JEE_PAPER2   -> "JEE";
            default           -> "ART";
        };
        long count = repo.count() + 1;
        return prefix + "-" + String.format("%03d", count);
    }
}
