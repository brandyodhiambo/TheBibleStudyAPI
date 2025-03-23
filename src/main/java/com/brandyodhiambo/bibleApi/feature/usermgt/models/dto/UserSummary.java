package com.brandyodhiambo.bibleApi.feature.usermgt.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserSummary {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
}