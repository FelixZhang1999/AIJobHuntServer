package com.example.assist.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class JobRequest {
    private List<EducationData> education;
    private List<ExperienceData> experience;
    private String desiredTitle;
    private String website;
}
