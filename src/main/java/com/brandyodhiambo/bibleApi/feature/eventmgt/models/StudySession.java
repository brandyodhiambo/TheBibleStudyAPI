package com.brandyodhiambo.bibleApi.feature.eventmgt.models;

import com.brandyodhiambo.bibleApi.feature.groupmgt.models.Group;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "study_sessions")
public class StudySession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private LocalDate sessionDate;

    @Column(nullable = false)
    private LocalTime startTime;

    private LocalTime endTime;

    private String location;

    @Enumerated(EnumType.STRING)
    private SessionType type;

    @Enumerated(EnumType.STRING)
    private RecurrencePattern recurrencePattern;

    private LocalDate recurrenceEndDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by", nullable = false)
    private Users createdBy;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SessionRSVP> rsvps = new HashSet<>();

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper methods
    public void addRSVP(SessionRSVP rsvp) {
        this.rsvps.add(rsvp);
        rsvp.setSession(this);
    }

    public void removeRSVP(SessionRSVP rsvp) {
        this.rsvps.remove(rsvp);
        rsvp.setSession(null);
    }

    public boolean hasRSVPFromUser(Users user) {
        return this.rsvps.stream()
                .anyMatch(rsvp -> rsvp.getUser().equals(user));
    }

    public SessionRSVP getRSVPFromUser(Users user) {
        return this.rsvps.stream()
                .filter(rsvp -> rsvp.getUser().equals(user))
                .findFirst()
                .orElse(null);
    }
}