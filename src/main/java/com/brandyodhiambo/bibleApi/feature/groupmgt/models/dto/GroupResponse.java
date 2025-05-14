package com.brandyodhiambo.bibleApi.feature.groupmgt.models.dto;

import com.brandyodhiambo.bibleApi.feature.groupmgt.models.GroupType;
import com.brandyodhiambo.bibleApi.feature.usermgt.models.dto.UserSummary;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupResponse implements Serializable {

    private Long id;

    private String name;

    private String description;

    private String location;

    private LocalTime meetingTime;

    private GroupType type;

    private UserSummary leader;

    private Set<UserSummary> members;

    private LocalDate createdAt;

    private LocalDate updatedAt;

    private int memberCount;
}
