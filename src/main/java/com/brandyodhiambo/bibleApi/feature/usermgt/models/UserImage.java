package com.brandyodhiambo.bibleApi.feature.usermgt.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;


@Entity
@Table(name = "user_images")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "imagedata", length = 1000)
    private byte[] imageData;
}
