package com.example.assist.helper;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.example.assist.model.JobContent;
import com.example.assist.model.JobRequest;

public class ToStringConverter {
    public static String JobRequestToResume(final JobRequest request) {
        return "Education: " + request.getEducation() + "\nExperience: " + request.getExperience() +
            "\nSkils: " + request.getSkills();
    }

    public static String JobContentToString(final JobContent content) {
        return "Title: " + content.getTitle() + "\nCompany: " + content.getCompany() +
            "\nLocation: " + content.getLocation() + "\nDescription: " + content.getDescription();
    }

    public static String JobContentsToString(final List<JobContent> jobs) {
        return IntStream.range(0, jobs.size())
            .mapToObj(i -> "Job Posting #" + (i + 1) + ":\n" + ToStringConverter.JobContentToString(jobs.get(i)))
            .collect(Collectors.joining(System.lineSeparator()));
    }
}
