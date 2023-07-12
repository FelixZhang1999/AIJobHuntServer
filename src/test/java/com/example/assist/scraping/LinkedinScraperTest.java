package com.example.assist.scraping;

import com.example.assist.model.JobContent;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LinkedinScraperTest {
    
    private final String LOCATION = "CALIFORNIA";
    private final String TITLE = "Software Engineer";

    private LinkedinScraper linkedinScraper = new LinkedinScraper();

    @Test
    void test_scrapeJobs_zeroSize_returnEmptyList() {
        final List<JobContent> jobs = linkedinScraper.scrapeJobs(TITLE, LOCATION, 0);
        assertEquals(jobs.size(), 0);
    }

    @Test
    void test_scrapeJobs_twoJobs_success() {
        final List<JobContent> jobs = linkedinScraper.scrapeJobs(TITLE, LOCATION, 2);
        assertEquals(jobs.size(), 2);
        jobs.stream().forEach(job -> checkJobContent(job));
    }

    private void checkJobContent(JobContent job) {
        assertNotNull(job.getCompany());
        assertNotNull(job.getLocation());
        assertNotNull(job.getTitle());
        assertNotNull(job.getDescription());

        assertNotEquals(job.getCompany(), "");
        assertNotEquals(job.getLocation(), "");
        assertNotEquals(job.getTitle(), "");
        assertNotEquals(job.getDescription(), "");
    }
}
