package com.example.assist.controller;

import com.example.assist.api.ChatGPTApi;
import com.example.assist.exception.InvalidRequestException;
import com.example.assist.model.EducationData;
import com.example.assist.model.ExperienceData;
import com.example.assist.model.JobContent;
import com.example.assist.model.JobRequest;
import com.example.assist.model.JobResponse;
import com.example.assist.scraping.LinkedinScraper;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AssistControllerTest {

    private final List<JobContent> JOBS = ImmutableList.of(JobContent.builder().title("SDE").build(),
                                                            JobContent.builder().title("SDM").build());

    private final String RESUME_STRING = "Education: UCSD, Not graduated\nExperience: Amazon ";

    private AssistController assistController;

    @Mock
    private ChatGPTApi chatGPTApi;

    @Mock
    private LinkedinScraper linkedinScraper;

    private JobRequest resume;

    @BeforeEach
    void init() {
        when(linkedinScraper.scrapeJobs(eq("SDE"), eq("US"), anyInt())).thenReturn(JOBS);
        doAnswer(invocation  -> {
            List<JobContent> myJobs = invocation.getArgument(1);
            myJobs.get(0).setOverallRating(4);
            myJobs.get(1).setOverallRating(5);
            return null;
        }).when(chatGPTApi).rateJobs(eq(RESUME_STRING), any());
        assistController = new AssistController(chatGPTApi, linkedinScraper);
        resume = JobRequest.builder()
                            .desiredTitle("SDE")
                            .website("Linkedin")
                            .location("US")
                            .education(ImmutableList.of(EducationData.builder()
                                                                        .school("UCSD")
                                                                        .build()))
                            .experience(ImmutableList.of(ExperienceData.builder()
                                                                        .company("Amazon")
                                                                        .build()))
                            .build();
    }

    @Test
    void test_homePage_returnHome() {
        assertEquals("home", assistController.homePage());
    }

    @Test
    void test_submit_noTitle_fails() {
        assertThrows(InvalidRequestException.class, () -> {
            assistController.submitForm(JobRequest.builder().build());
        });
    }

    @Test
    void test_submit_goodRequest_success() {
        final ResponseEntity response = assistController.submitForm(resume);
        verify(linkedinScraper, times(1))
                    .scrapeJobs(eq("SDE"), eq("US"), anyInt());
        verify(chatGPTApi, times(1)).rateJobs(eq(RESUME_STRING), any());
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        final List<JobContent> jobContents = ((JobResponse) response.getBody()).getJobs();
        assertEquals(jobContents.get(0).getOverallRating(), 5);
    }

    @Test
    void test_submit_badWebsites_success() {
        resume.setWebsite("Indeed");
        assistController.submitForm(resume);
        verify(linkedinScraper, times(0))
                    .scrapeJobs(anyString(), anyString(), anyInt());

        resume.setWebsite("Google");
        assistController.submitForm(resume);
        verify(linkedinScraper, times(1))
                    .scrapeJobs(eq("SDE"), eq("US"), anyInt());
    }
}
