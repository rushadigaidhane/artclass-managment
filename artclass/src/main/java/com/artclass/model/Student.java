package com.artclass.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String studentId; // custom student ID like ART-001

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Phone number is required")
    private String phone;

    private String parentsPhone; // optional

    // Class type
    @Enumerated(EnumType.STRING)
    private ClassType classType;

    // Sub-type for classes that have options (monthly/course, normal/bridal)
    private String classSubType; // "monthly", "course", "normal", "bridal"

    // Fees
    private Double classFees;
    private Double advanceFees;
    private Double completionFees;
    private Boolean feesComplete = false;

    // Timing slot
    private String timingSlot; // e.g. "Mon-Wed-Fri 10:00-11:00 AM"

    // Status
    private Boolean active = true;

    // Enrollment date
    private LocalDate enrollmentDate;

    // Notes
    @Column(length = 500)
    private String notes;

    // Audit
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (enrollmentDate == null) enrollmentDate = LocalDate.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ClassType {
        ELEMENTARY("Elementary"),
        INTERMEDIATE("Intermediate"),
        DRAWING("Drawing"),
        SKETCHING("Sketching"),
        MEHENDI("Mehendi"),
        RANGOLI("Rangoli"),
        CRAFT("Craft"),
        BFA_ENTRANCE("BFA Entrance"),
        JEE_PAPER2("JEE Paper 2 Entrance");

        private final String displayName;
        ClassType(String displayName) { this.displayName = displayName; }
        public String getDisplayName() { return displayName; }
    }

    // Helper: pending fees
    public Double getPendingFees() {
        double total = (classFees != null ? classFees : 0)
                     + (completionFees != null ? completionFees : 0);
        double paid  = (advanceFees != null ? advanceFees : 0);
        return Math.max(0, total - paid);
    }

    public Double getTotalFees() {
        return (classFees != null ? classFees : 0)
             + (completionFees != null ? completionFees : 0);
    }
}
