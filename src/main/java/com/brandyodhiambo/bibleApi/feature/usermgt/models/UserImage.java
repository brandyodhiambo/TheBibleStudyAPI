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

    @Column(unique = true, nullable = false)
    private String username;

    @Lob
    @Column(name = "imagedata", length = 1000)
    private byte[] imageData;
}


