package com.example.assist.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Request of submit API call.
 */
@AllArgsConstructor(onConstructor_ = {@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)})
@Builder
@Data
public class JobRequest {
    private int nextStart;
    private List<EducationData> education;
    private List<ExperienceData> experience;
    private String desiredTitle;
    private String location;
    private String website;
}
