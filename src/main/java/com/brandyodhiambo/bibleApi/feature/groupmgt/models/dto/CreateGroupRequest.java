package com.brandyodhiambo.bibleApi.feature.groupmgt.models.dto;

import com.brandyodhiambo.bibleApi.feature.groupmgt.models.GroupType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateGroupRequest implements Serializable {

    @NotBlank(message = "Group name is required")
    private String name;

    private String description;

    private String location;

    private MeetingTime meetingTime;

    @NotNull(message = "Group type is required")
    private GroupType type;
}
