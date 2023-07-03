package com.example.assist.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;

// Response data class
@Builder
@Data
public class JobResponse {
    private List<JobContent> jobs;
}
