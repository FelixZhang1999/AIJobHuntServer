package com.example.assist.model;

import lombok.Builder;
import lombok.Data;

/**
 * Job posting information.
 */
@Builder
@Data
public class JobContent {
    private int experienceRating;
    private int overallRating;
    private int qualificationsRating;
    private int skillsRating;
    private String company;
    private String description;
    private String location;
    private String title;
    private String url;
}
