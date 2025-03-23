package com.brandyodhiambo.bibleApi.feature.groupmgt.models.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupMemberRequest {
    
    @NotBlank(message = "Username is required")
    private String username;
}