package com.example.assist.helper;

import com.example.assist.model.JobContent;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RatingHelperTest {

    private final List<JobContent> JOBS_1 = ImmutableList.of(JobContent.builder()
                                                                        .title("1")
                                                                        .overallRating(3)
                                                                        .build(),
                                                            JobContent.builder()
                                                                        .title("2")
                                                                        .overallRating(4)
                                                                        .build());

    private final List<JobContent> JOBS_2 = ImmutableList.of(JobContent.builder()
                                                                        .title("1")
                                                                        .overallRating(4)
                                                                        .qualificationsRating(5)
                                                                        .build(),
                                                            JobContent.builder()
                                                                        .title("2")
                                                                        .overallRating(4)
                                                                        .qualificationsRating(4)
                                                                        .build());

    @Test
    void test_findHighestOverallScore_differenceOverall_success() {
        JobContent job = RatingHelper.findHighestOverallScore(JOBS_1);
        assertEquals(job.getTitle(), "2");
    }

    @Test
    void test_findHighestOverallScore_sameOverall_success() {
        JobContent job = RatingHelper.findHighestOverallScore(JOBS_2);
        assertEquals(job.getTitle(), "1");
    }
}
