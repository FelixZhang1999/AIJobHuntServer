package com.example.assist.controller;

import com.example.assist.model.JobContent;
import com.example.assist.api.ChatGPTApi;
import com.example.assist.enums.WebsiteEnum;
import com.example.assist.exception.InvalidRequestException;
import com.example.assist.helper.RatingHelper;
import com.example.assist.helper.StringConverter;
import com.example.assist.model.JobRequest;
import com.example.assist.model.JobResponse;
import com.example.assist.scraping.LinkedinScraper;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.RateLimiter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Main controller for APIs.
 */
@Controller
public class AssistController {

    private final ChatGPTApi chatGPTApi;
    private final int scrapeSize = 5;
    private final Logger logger = LoggerFactory.getLogger(AssistController.class);
    private final LinkedinScraper linkedinScraper;
    private final RateLimiter rateLimiter;
    
    public AssistController(final ChatGPTApi chatGPTApi,
                            final LinkedinScraper linkedinScraper,
                            final RateLimiter rateLimiter) {
        this.chatGPTApi = chatGPTApi;
        this.linkedinScraper = linkedinScraper;
        this.rateLimiter = rateLimiter;
    }

    @GetMapping("/")
    public String homePage() {
        return "home";
    }

    /**
     * Submit API call. This API is called to search for suitable job postings.
     * @param resumeData The resume of the user.
     * @return Suitable job postings.
     */
    @PostMapping("/submit")
    public ResponseEntity<JobResponse> submitForm(@RequestBody final JobRequest resumeData) {
        if (!rateLimiter.tryAcquire()) {
            return buildErrorResponse("Too many requests, please try again.");
        }
        logger.info(resumeData.toString());
        valudateJobRequest(resumeData);
        final List<JobContent> jobs;
        WebsiteEnum website;
        try {
            website = WebsiteEnum.valueOf(resumeData.getWebsite());
        } catch (final IllegalArgumentException | NullPointerException e) {
            logger.warn("Unknown website String: " + resumeData.getWebsite());
            website = WebsiteEnum.Linkedin;
        }
        if (website.equals(WebsiteEnum.Linkedin)) {
            jobs = linkedinScraper.scrapeJobs(resumeData.getDesiredTitle(), resumeData.getLocation(), scrapeSize);
        } else {
            jobs = ImmutableList.of();
        }
        if (jobs.size() == 0) {
            return buildErrorResponse("Could not find any job postings, please try again.");
        }
        logger.info("Size of job: " + jobs.size());
        logger.info(jobs.toString());
        logger.info(StringConverter.JobRequestToString(resumeData));
        chatGPTApi.rateJobs(StringConverter.JobRequestToString(resumeData), jobs);
        final JobContent bestJob = RatingHelper.findHighestOverallScore(jobs);
        return ResponseEntity.status(HttpStatus.OK)
                            .body(JobResponse.builder().jobs(ImmutableList.of(bestJob)).error(false).build());
    }

    /**
     * Validate JobRequest.
     * @param resumeData The resume of the user.
     */
    private void valudateJobRequest(final JobRequest resumeData) {
        if (resumeData.getDesiredTitle() == null) {
            throw new InvalidRequestException("No desired title.");
        }
    }

    private ResponseEntity<JobResponse> buildErrorResponse(final String message) {
        return ResponseEntity.status(HttpStatus.OK)
                            .body(JobResponse.builder()
                                            .message(message)
                                            .error(true)
                                            .build());
    }
}
