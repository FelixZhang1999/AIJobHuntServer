package com.example.assist.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * Request of submit API call.
 */
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
