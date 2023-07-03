package com.example.assist.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ExperienceData {
    private String company;
    private String description;
    private String duration;
    private String title;
}
