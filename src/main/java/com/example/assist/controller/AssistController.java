package com.example.assist.controller;

import com.example.assist.model.JobContent;
import com.example.assist.api.ChatGPTApi;
import com.example.assist.enums.WebsiteEnum;
import com.example.assist.helper.RatingHelper;
import com.example.assist.helper.StringConverter;
import com.example.assist.model.JobRequest;
import com.example.assist.model.JobResponse;
import com.example.assist.scraping.LinkedinScraper;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AssistController {

    private final ChatGPTApi chatGPTApi;
    private final int scrapeSize = 5;
    private final Logger logger = LoggerFactory.getLogger(AssistController.class);
    private final LinkedinScraper linkedinScraper;

    public AssistController(final ChatGPTApi chatGPTApi, final LinkedinScraper linkedinScraper) {
        this.chatGPTApi = chatGPTApi;
        this.linkedinScraper = linkedinScraper;
    }

    @GetMapping("/")
    public String homePage() {
        return "home";
    }

    @PostMapping("/submit")
    public ResponseEntity<JobResponse> submitForm(@RequestBody JobRequest resumeData) {
        logger.info(resumeData.toString());
        return ResponseEntity.status(HttpStatus.OK)
                            .body(getJobResponse(resumeData));
    }

    public JobResponse getJobResponse(JobRequest resumeData) {
        final List<JobContent> jobs;
        WebsiteEnum website;
        try {
            website = WebsiteEnum.valueOf(resumeData.getWebsite());
        } catch (IllegalArgumentException e) {
            website = WebsiteEnum.Linkedin;
        }
        if (website.equals(WebsiteEnum.Linkedin)) {
            jobs = linkedinScraper.scrapeJobs(resumeData.getDesiredTitle(), scrapeSize);
        } else {
            jobs = ImmutableList.of();
        }
        logger.info("Size of job: " + jobs.size());
        logger.info(jobs.toString());
        logger.info(StringConverter.JobRequestToString(resumeData));
        chatGPTApi.rateJobs(StringConverter.JobRequestToString(resumeData), jobs);
        final JobContent bestJob = RatingHelper.findHighestOverallScore(jobs);
        return JobResponse.builder().jobs(ImmutableList.of(bestJob)).build();
    }
}
