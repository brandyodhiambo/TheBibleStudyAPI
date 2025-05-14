package com.brandyodhiambo.bibleApi.feature.usermgt.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSummary implements Serializable {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
}
