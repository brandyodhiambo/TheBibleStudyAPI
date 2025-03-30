package com.brandyodhiambo.bibleApi.feature.groupmgt.models.dto;

import com.brandyodhiambo.bibleApi.feature.groupmgt.models.GroupType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGroupRequest {

    private String name;

    private String description;

    private String location;

    private MeetingTime meetingTime;

    private GroupType type;
}
