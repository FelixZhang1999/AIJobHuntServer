package com.example.assist.helper;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.example.assist.model.EducationData;
import com.example.assist.model.ExperienceData;
import com.example.assist.model.JobContent;
import com.example.assist.model.JobRequest;

public class StringConverter {
    
    public static String JobRequestToString(final JobRequest resumeData) {
        return "Education: " + educationDataListToString(resumeData.getEducation()) +
                "\nExperience: " + experienceDataListToString(resumeData.getExperience());
    }

    public static String JobContentListToResult(final List<JobContent> contents) {
        return contents.stream()
                        .map(StringConverter::JobContentToString)
                        .collect(Collectors.joining("\n"));
    }

    public static String JobContentToResult(final JobContent content) {
        return "Title: " + content.getTitle() + "\nCompany: " + content.getCompany() +
            "\nLocation: " + content.getLocation() + "\nLink: " + content.getUrl();
    }

    public static String educationDataListToString(final List<EducationData> education) {
        return education.stream()
                        .map(StringConverter::educationDataToString)
                        .collect(Collectors.joining("\n"));
    }

    public static String experienceDataListToString(final List<ExperienceData> experienc) {
        return experienc.stream()
                        .map(StringConverter::experienceDataToString)
                        .collect(Collectors.joining("\n"));
    }

    public static String educationDataToString(final EducationData educationData) {
        String returnString = educationData.getDegree() + " " + educationData.getMajor() + " " +
            educationData.getSchool() + ", ";
        if (educationData.isGraduated()) {
            returnString += "Graduated";
        } else {
            returnString += "Not graduated";
        }
        return returnString;
    }

    public static String experienceDataToString(final ExperienceData experienceData) {
        return experienceData.getCompany() + " " + experienceData.getTitle() + " " +
            experienceData.getDuration() + ", worked on " + experienceData.getDescription();
    }

    public static String JobContentsToString(final List<JobContent> jobs) {
        return IntStream.range(0, jobs.size())
            .mapToObj(i -> "Job Posting #" + (i + 1) + ":\n" + StringConverter.JobContentToString(jobs.get(i)))
            .collect(Collectors.joining(System.lineSeparator()));
    }

    public static String JobContentToString(final JobContent content) {
        return "Title: " + content.getTitle() + "\nCompany: " + content.getCompany() +
            "\nLocation: " + content.getLocation() + "\nDescription: " + content.getDescription();
    }
}
