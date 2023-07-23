package com.example.assist.helper;

import com.google.common.collect.ImmutableList;
import java.util.List;

/**
 * This class contains methods to reduce the length of descriptions.
 */
public class DescriptionShortener {

    // Description is informative after these keywords
    final static List<String> KEYWORDS = ImmutableList.of("responsibilities",
                                                        "what you\u2019ll",
                                                        "what you will",
                                                        "we're looking for",
                                                        "we are looking for",
                                                        "requirements",
                                                        "qualifications");

    /**
     * Shorten description
     * @param description
     * @return the short description
     */
    public static String shortenDescription(final String description) {
        if (description == null || description.length() == 0) {
            return description;
        }
        final String descriptionLower = description.toLowerCase();
        for (final String keyword : KEYWORDS) {
            int index = descriptionLower.indexOf(keyword);
            if (index != -1) {
                return description.substring(index);
            }
        }
        return description;
    }
}
