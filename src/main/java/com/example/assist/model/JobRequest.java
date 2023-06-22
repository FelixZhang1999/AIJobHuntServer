package com.example.assist.model;

import com.example.assist.enums.WebsiteEnum;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class JobRequest {
    private String education;
    private String experience;
    private String jobTitle;
    private String skills;
    private WebsiteEnum website;
}
