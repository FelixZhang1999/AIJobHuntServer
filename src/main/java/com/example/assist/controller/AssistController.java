package com.example.assist.controller;

import com.example.assist.model.JobContent;
import com.example.assist.api.ChatGPTApi;
import com.example.assist.enums.WebsiteEnum;
import com.example.assist.helper.RatingHelper;
import com.example.assist.helper.ToStringConverter;
import com.example.assist.model.JobRequest;
import com.example.assist.model.JobResponse;
import com.example.assist.scraping.LinkedinScraper;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AssistController {

    private final ChatGPTApi chatGPTApi;
    private final int scrapeSize = 2;
    private final Logger logger = LoggerFactory.getLogger(AssistController.class);
    private final LinkedinScraper linkedinScraper;

    public AssistController(final ChatGPTApi chatGPTApi, final LinkedinScraper linkedinScraper) {
        this.chatGPTApi = chatGPTApi;
        this.linkedinScraper = linkedinScraper;
    }

    /**
     * @return
     */
    @PostMapping
    public JobResponse getJobURL(@RequestBody JobRequest request) throws IOException {
        final List<JobContent> jobs;
        if (request.getWebsite().equals(WebsiteEnum.Linkedin)) {
            jobs = linkedinScraper.scrapeJobs(request.getJobTitle(), scrapeSize);
        } else {
            jobs = ImmutableList.of();
        }
        logger.info("Size of job: " + jobs.size());
        chatGPTApi.rateJobs(ToStringConverter.JobRequestToResume(request), jobs);
        final JobContent bestJob = RatingHelper.findHighestOverallScore(jobs);
        return JobResponse.builder().jobs(ImmutableList.of(bestJob)).build();
    }
}
