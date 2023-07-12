package com.example.assist.helper;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.example.assist.model.EducationData;
import com.example.assist.model.ExperienceData;
import com.example.assist.model.JobContent;
import com.example.assist.model.JobRequest;

/**
 * Helper methods related to Strings
 */
public class StringConverter {
    
    public static String JobRequestToString(final JobRequest resumeData) {
        return "Education: " + educationDataListToString(resumeData.getEducation()) +
                "\nExperience: " + experienceDataListToString(resumeData.getExperience());
    }

    private static String educationDataListToString(final List<EducationData> education) {
        return education.stream()
                        .map(StringConverter::educationDataToString)
                        .collect(Collectors.joining("\n"));
    }

    private static String experienceDataListToString(final List<ExperienceData> experienc) {
        return experienc.stream()
                        .map(StringConverter::experienceDataToString)
                        .collect(Collectors.joining("\n"));
    }

    private static String educationDataToString(final EducationData educationData) {
        String returnString = "";
        if (educationData.getDegree() != null) {
            returnString += educationData.getDegree() + " ";
        }
        if (educationData.getMajor() != null) {
            returnString += educationData.getMajor() + " ";
        }
        if (educationData.getSchool() != null) {
            returnString += educationData.getSchool();
        }
        if (educationData.isGraduated()) {
            returnString += ", Graduated";
        } else {
            returnString += ", Not graduated";
        }
        return returnString;
    }

    private static String experienceDataToString(final ExperienceData experienceData) {
        String returnString = "";
        if (experienceData.getCompany() != null) {
            returnString += experienceData.getCompany() + " ";
        }
        if (experienceData.getTitle() != null) {
            returnString += experienceData.getTitle() + " ";
        }
        if (experienceData.getDuration() != null) {
            returnString += experienceData.getDuration() + " ";
        }
        if (experienceData.getDuration() != null) {
            returnString += experienceData.getDuration();
        }
        if (experienceData.getDescription() != null) {
            returnString += ", worked on " + experienceData.getDescription();
        }
        return returnString;
    }

    public static String JobContentsToString(final List<JobContent> jobs) {
        return IntStream.range(0, jobs.size())
            .mapToObj(i -> "Job Posting #" + (i + 1) + ":\n" + StringConverter.JobContentToString(jobs.get(i)))
            .collect(Collectors.joining("\n"));
    }

    private static String JobContentToString(final JobContent content) {
        return "Title: " + content.getTitle() + "\nCompany: " + content.getCompany() +
            "\nLocation: " + content.getLocation() + "\nDescription: " + content.getDescription();
    }
}
