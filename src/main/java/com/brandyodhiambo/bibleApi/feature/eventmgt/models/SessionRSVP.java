package com.brandyodhiambo.bibleApi.feature.eventmgt.models;

import com.brandyodhiambo.bibleApi.feature.usermgt.models.Users;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "session_rsvps")
public class SessionRSVP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private StudySession session;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RSVPStatus status;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructor for creating a new RSVP
    public SessionRSVP(StudySession session, Users user, RSVPStatus status) {
        this.session = session;
        this.user = user;
        this.status = status;
    }

    // Constructor for creating a new RSVP with a comment
    public SessionRSVP(StudySession session, Users user, RSVPStatus status, String comment) {
        this.session = session;
        this.user = user;
        this.status = status;
        this.comment = comment;
    }
}