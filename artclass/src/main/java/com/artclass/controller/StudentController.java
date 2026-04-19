package com.artclass.controller;

import com.artclass.model.Student;
import com.artclass.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class StudentController {

    private final StudentService service;

    /* ── Login ─────────────────────────────────── */
    @GetMapping("/login")
    public String loginPage() { return "login"; }

    /* ── Dashboard / Home ───────────────────────── */
    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        model.addAttribute("totalStudents",   service.totalStudents());
        model.addAttribute("activeStudents",  service.activeStudents());
        model.addAttribute("inactiveStudents",service.inactiveStudents());
        model.addAttribute("pendingFees",     service.pendingFees());
        model.addAttribute("totalRevenue",    service.totalRevenue());
        model.addAttribute("totalCollected",  service.totalCollected());
        model.addAttribute("recentStudents",  service.getAll().stream().limit(5).toList());
        model.addAttribute("pendingList",     service.getPendingFees().stream().limit(5).toList());
        return "dashboard";
    }

    /* ── Student List ───────────────────────────── */
    @GetMapping("/students")
    public String list(@RequestParam(required = false) String q,
                       @RequestParam(required = false) String classType,
                       Model model) {
        var students = (q != null && !q.isBlank())
                ? service.search(q)
                : (classType != null && !classType.isBlank())
                    ? service.getByClass(Student.ClassType.valueOf(classType))
                    : service.getAll();

        model.addAttribute("students",   students);
        model.addAttribute("q",          q);
        model.addAttribute("classType",  classType);
        model.addAttribute("classTypes", Student.ClassType.values());
        return "students/list";
    }

    /* ── Add Student Form ───────────────────────── */
    @GetMapping("/students/new")
    public String newForm(Model model) {
        model.addAttribute("student",    new Student());
        model.addAttribute("classTypes", Student.ClassType.values());
        model.addAttribute("isEdit",     false);
        return "students/form";
    }

    /* ── Edit Student Form ──────────────────────── */
    @GetMapping("/students/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return service.getById(id).map(s -> {
            model.addAttribute("student",    s);
            model.addAttribute("classTypes", Student.ClassType.values());
            model.addAttribute("isEdit",     true);
            return "students/form";
        }).orElseGet(() -> {
            ra.addFlashAttribute("error", "Student not found.");
            return "redirect:/students";
        });
    }

    /* ── Save (Create / Update) ─────────────────── */
    @PostMapping("/students/save")
    public String save(@Valid @ModelAttribute("student") Student student,
                       BindingResult br,
                       Model model,
                       RedirectAttributes ra) {
        if (br.hasErrors()) {
            model.addAttribute("classTypes", Student.ClassType.values());
            model.addAttribute("isEdit", student.getId() != null);
            return "students/form";
        }
        service.save(student);
        ra.addFlashAttribute("success", student.getId() == null
                ? "Student added successfully!"
                : "Student updated successfully!");
        return "redirect:/students";
    }

    /* ── View Student Detail ────────────────────── */
    @GetMapping("/students/{id}")
    public String view(@PathVariable Long id, Model model, RedirectAttributes ra) {
        return service.getById(id).map(s -> {
            model.addAttribute("student", s);
            return "students/detail";
        }).orElseGet(() -> {
            ra.addFlashAttribute("error", "Student not found.");
            return "redirect:/students";
        });
    }

    /* ── Delete ─────────────────────────────────── */
    @PostMapping("/students/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        service.delete(id);
        ra.addFlashAttribute("success", "Student record deleted.");
        return "redirect:/students";
    }

    /* ── Toggle Status ──────────────────────────── */
    @PostMapping("/students/toggle/{id}")
    public String toggle(@PathVariable Long id,
                         @RequestParam(defaultValue = "/students") String redirect,
                         RedirectAttributes ra) {
        service.toggleStatus(id);
        ra.addFlashAttribute("success", "Student status updated.");
        return "redirect:" + redirect;
    }

    /* ── Mark Fees Complete ─────────────────────── */
    @PostMapping("/students/fees/{id}")
    public String fees(@PathVariable Long id,
                       @RequestParam boolean complete,
                       RedirectAttributes ra) {
        service.markFeesComplete(id, complete);
        ra.addFlashAttribute("success", "Fees status updated.");
        return "redirect:/students/" + id;
    }

    /* ── Pending Fees Report ────────────────────── */
    @GetMapping("/reports/pending-fees")
    public String pendingFeesReport(Model model) {
        model.addAttribute("students", service.getPendingFees());
        return "reports/pending-fees";
    }
}
