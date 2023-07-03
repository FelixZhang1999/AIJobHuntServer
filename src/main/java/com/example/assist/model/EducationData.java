package com.example.assist.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class EducationData {
    private boolean graduated;
    private String degree;
    private String major;
    private String school;
}
