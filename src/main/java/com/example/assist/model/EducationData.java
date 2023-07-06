package com.example.assist.model;

import lombok.Builder;
import lombok.Data;

/**
 * The builder of education data.
 */
@Builder
@Data
public class EducationData {
    private boolean graduated;
    private String degree;
    private String major;
    private String school;
}
