package com.example.assist.helper;

import com.example.assist.model.JobContent;
import com.example.assist.model.JobRequest;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class StringConverterTest {

    private final List<JobContent> JOBCONTENT = ImmutableList.of(
                                                    JobContent.builder()
                                                                .title("Title1")
                                                                .company("Company1")
                                                                .location("Location1")
                                                                .url("URL1")
                                                                .description("Description1")
                                                                .build(),
                                                    JobContent.builder()
                                                                .title("Title2")
                                                                .company("Company2")
                                                                .location("Location2")
                                                                .description("Description2")
                                                                .url("URL2")
                                                                .build());
    private final JobRequest JOBREQUEST = JobRequest.builder()
                                                    .education(ImmutableList.of())
                                                    .experience(ImmutableList.of()).build();
    private final String EXPECTED_JOBCONTENT_STRING = "Job Posting #1:\n" +
            "Title: Title1\n" +
            "Company: Company1\n" +
            "Location: Location1\n" +
            "Description: Description1\n" +
            "Job Posting #2:\n" +
            "Title: Title2\n" +
            "Company: Company2\n" +
            "Location: Location2\n" +
            "Description: Description2";
    private final String EXPECTED_JOBREQUEST_STRING = "Education: \nExperience: ";

    @Test
    public void test_JobRequestToString() {
        String result = StringConverter.JobRequestToString(JOBREQUEST);
        assertEquals(EXPECTED_JOBREQUEST_STRING, result);
    }

    @Test
    public void test_JobContentsToString() {
        String result = StringConverter.JobContentsToString(JOBCONTENT);
        assertEquals(EXPECTED_JOBCONTENT_STRING, result);
    }
}
