package com.brandyodhiambo.bibleApi.feature.groupmgt.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum GroupType {
    VIRTUAL,
    IN_PERSON,
    PHYSICAL;

    @JsonCreator
    public static GroupType fromString(String value) {
        if (value == null) {
            return null;
        }

        // Handle "PHYSICAL" as an alias for "IN_PERSON"
        if (value.equalsIgnoreCase("PHYSICAL")) {
            return IN_PERSON;
        }

        try {
            return GroupType.valueOf(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @JsonValue
    public String getValue() {
        // Ensure consistent representation in JSON
        if (this == PHYSICAL) {
            return IN_PERSON.name();
        }
        return this.name();
    }
}
