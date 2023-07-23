package com.example.assist.helper;

import com.example.assist.model.JobContent;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to ratings.
 */
public class RatingHelper {
    
    /**
     * Find the highest overall score job among a list of jobs.
     * @param jobs The list of jobs to look for.
     * @return The highest score job.
     */
    public static JobContent findHighestOverallScore(List<JobContent> jobs) {
        JobContent highestScoreJob = null;
        int highestOverallRating = Integer.MIN_VALUE;
        int highestScoreSum = Integer.MIN_VALUE;

        for (final JobContent job : jobs) {
            int overallScore = job.getOverallRating();
            int scoreSum = job.getExperienceRating() + job.getSkillsRating() + job.getQualificationsRating();

            if (overallScore > highestOverallRating || (overallScore == highestOverallRating && scoreSum > highestScoreSum)) {
                highestOverallRating = overallScore;
                highestScoreSum = scoreSum;
                highestScoreJob = job;
            }
        }

        return highestScoreJob;
    }

    /**
     * Find jobs with overall score 4 and above among a list of jobs.
     * @param jobs The list of jobs to look for.
     * @return The high score jobs.
     */
    public static List<JobContent> findHighOverallScores(List<JobContent> jobs) {
        List<JobContent> highScoreJobs = new ArrayList<JobContent>();
        for (final JobContent job : jobs) {
            if (job.getOverallRating() >= 4) {
                highScoreJobs.add(job);
            }
        }
        return highScoreJobs;
    }
}
