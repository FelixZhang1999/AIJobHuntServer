package com.example.assist.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * Response of submit API call.
 */
@Builder
@Data
public class JobResponse {
    private boolean error;
    private int nextStart;
    private List<JobContent> jobs;
    private String message;
}
