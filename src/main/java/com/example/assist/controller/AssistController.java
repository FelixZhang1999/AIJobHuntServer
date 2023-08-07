package com.example.assist.controller;

import com.example.assist.model.EducationData;
import com.example.assist.model.ExperienceData;
import com.example.assist.model.JobContent;
import com.example.assist.api.ChatGPTApi;
import com.example.assist.enums.WebsiteEnum;
import com.example.assist.helper.DescriptionShortener;
import com.example.assist.helper.RatingHelper;
import com.example.assist.helper.StringConverter;
import com.example.assist.model.JobRequest;
import com.example.assist.model.JobResponse;
import com.example.assist.scraping.LinkedinScraper;
import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.RateLimiter;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Main controller for APIs.
 */
@RestController
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

    /**
     * Submit API call. This API is called to search for suitable job postings.
     * @param resumeData The resume of the user.
     * @return Suitable job postings.
     */
    @PostMapping("/submit")
    public ResponseEntity<JobResponse> submitForm(@RequestBody final JobRequest resumeData) {
        if (!rateLimiter.tryAcquire()) {
            return buildErrorResponse("Too many requests. Please try again.", resumeData.getNextStart());
        }
        logger.info(resumeData.toString());
        if (!valudateJobRequest(resumeData)) {
            return buildErrorResponse("Invalid request. Please try again.", resumeData.getNextStart());
        }
        if (resumeData.getNextStart() >= 25) {
            return buildErrorResponse("Too many same requests. Please try a different title or location.",
                                        resumeData.getNextStart());
        }
        
        final List<JobContent> jobs = getJobList(resumeData);
        if (jobs.size() == 0) {
            return buildErrorResponse("Could not find any job postings. Please try a different title or location.",
                                        resumeData.getNextStart() + scrapeSize);
        }
        logger.info("Size of job: " + jobs.size());
        logger.info(jobs.toString());
        logger.info(StringConverter.JobRequestToString(resumeData));
        chatGPTApi.rateJobs(StringConverter.JobRequestToString(resumeData), jobs);
        final List<JobContent> bestJobs = RatingHelper.findHighOverallScores(jobs);
        if (bestJobs.size() == 0) {
            return buildErrorResponse("Could not find any job postings. Please try again.",
                                            resumeData.getNextStart() + scrapeSize);
        }
        // Set Description to empty to reduce data
        bestJobs.forEach(job -> job.setDescription(""));
        return ResponseEntity.status(HttpStatus.OK)
                            .body(JobResponse.builder()
                                            .jobs(bestJobs)
                                            .error(false)
                                            .nextStart(resumeData.getNextStart() + scrapeSize)
                                            .build());
    }

    /**
     * Scrape a list of jobs and modify it according to needs.
     * @param resumeData User resume
     * @return a list of jobs
     */
    private List<JobContent> getJobList(final JobRequest resumeData) {
        final List<JobContent> jobs;
        WebsiteEnum website;
        try {
            website = WebsiteEnum.valueOf(resumeData.getWebsite());
        } catch (final IllegalArgumentException | NullPointerException e) {
            logger.warn("Unknown website String: " + resumeData.getWebsite());
            website = WebsiteEnum.Linkedin;
        }
        if (website.equals(WebsiteEnum.Linkedin)) {
            jobs = linkedinScraper.scrapeJobs(resumeData.getDesiredTitle(), resumeData.getLocation(),
                                                scrapeSize, resumeData.getNextStart());
        } else {
            jobs = ImmutableList.of();
        }

        jobs.forEach(job -> {
            job.setDescription(DescriptionShortener.shortenDescription(job.getDescription()));
        });
        final Iterator<JobContent> iterator = jobs.iterator();
        while (iterator.hasNext()) {
            final JobContent obj = iterator.next();
            if (obj.getDescription() != null && obj.getDescription().length() > 4000) {
                iterator.remove();
            }
        }
        return jobs;
    }

    /**
     * Validate JobRequest.
     * @param resumeData The resume of the user.
     * @return whether is valid or not
     */
    private boolean valudateJobRequest(final JobRequest resumeData) {
        if (resumeData.getDesiredTitle() == null || resumeData.getDesiredTitle().length() == 0 ||
            resumeData.getDesiredTitle().length() > 40 ||
            stringLongerThan(resumeData.getLocation(), 40) ||
            !validateEducation(resumeData.getEducation()) || !validateExperience(resumeData.getExperience())) {
            return false;
        }
        return true;
    }

    private boolean validateEducation(final List<EducationData> education) {
        for (final EducationData educationData : education){
            if (stringLongerThan(educationData.getSchool(), 40) ||
                stringLongerThan(educationData.getMajor(), 40)) {
                return false;
            }
        }
        return true;
    }

    private boolean validateExperience(final List<ExperienceData> experience) {
        for (final ExperienceData experienceData : experience){
            if (stringLongerThan(experienceData.getCompany(), 40) ||
                stringLongerThan(experienceData.getTitle(), 40) ||
                stringLongerThan(experienceData.getDuration(), 20) ||
                stringLongerThan(experienceData.getDescription(), 100)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Build response for submit request.
     * @param message The message in the response.
     * @return
     */
    private ResponseEntity<JobResponse> buildErrorResponse(final String message, final int nextStart) {
        return ResponseEntity.status(HttpStatus.OK)
                            .body(JobResponse.builder()
                                            .message(message)
                                            .nextStart(nextStart)
                                            .error(true)
                                            .build());
    }

    /**
     * If a string is not null, it should not be longer than.
     * @param string The string to validate.
     * @param length The max length of the string.
     * @return whether is valid or not
     */
    private boolean stringLongerThan(final String string, final int length) {
        return string != null && string.length() > 30;
    }
}
