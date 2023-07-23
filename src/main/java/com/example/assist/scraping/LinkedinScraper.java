package com.example.assist.scraping;

import com.example.assist.model.JobContent;
import com.google.common.collect.ImmutableList;
import io.micrometer.common.lang.Nullable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Scrape from linkedin
 */
@Component
public class LinkedinScraper extends BaseScraper {

    private final int MAX_RETRIES = 3;
    private final int RETRY_DELAY_MILLIS = 1000;
    private final Logger logger = LoggerFactory.getLogger(LinkedinScraper.class);
    private final String BASE_URL = "https://www.linkedin.com/jobs/search?";

    /**
     * Scrape job postings.
     * @param title The desired title of jobs.
     * @param location The desired location of jobs.
     * @param size The number of jobs to scrape.
     * @return A list of job postings.
     */
    public List<JobContent> scrapeJobs(final String title, @Nullable final String location, final int size,
                                        final int nextStart){
        if (size <= 0) {
            return ImmutableList.of();
        }
        final String url = getFullUrl(title, location);
        final List<String> links = getLinksFromURL(url, size, nextStart);
        return links.stream().map(this::scrapeJobContent).collect(Collectors.toList());
    }

    /**
     * Create full url according to title and location.
     * @param title The desired title of jobs.
     * @param location The desired location of jobs.
     * @return The full url.
     */
    protected String getFullUrl(final String title, @Nullable final String location) {
        String fullUrl = BASE_URL + "keywords=" + title.replace(" ", "%2B");
        if (location != null){
            fullUrl += "&location=" + location.replace(" ", "%2B");
        }
        return fullUrl;
    }

    /**
     * Get job posting links from Linkedin search url.
     * @param url The linkedin search url.
     * @param size The number of jobs to scrape.
     * @return A list of job postings url.
     */
    protected List<String> getLinksFromURL(final String url, final int size, final int nextStart) {
        List<String> links = new ArrayList<String>();
        try {
            final Document document = Jsoup.connect(url).get();
            final Elements linkElements = document.select("a.base-card__full-link");
            for (int i = nextStart; i < 25; i++) {
                final Element linkElement = linkElements.get(i);
                links.add(linkElement.attr("href"));
                if (links.size() >= size) {
                    break;
                }
            }
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
        return links;
    }

    /**
     * Scrape job posting url to get job posting contents.
     * @param url A job postings url
     * @return The job posting contents.
     */
    protected JobContent scrapeJobContent(final String url){
        final JobContent jobContent = JobContent.builder().url(url).build();
        try {
            final Document document = fetchUrlWithRetries(url, MAX_RETRIES, RETRY_DELAY_MILLIS);
            final Element jobTitleElement = document.selectFirst("h1.top-card-layout__title");
            jobContent.setTitle(jobTitleElement.text());

            final Element companyElement = document.selectFirst("a.topcard__org-name-link");
            final String companyName = companyElement.text().trim();
            jobContent.setCompany(companyName);

            final Element locationElement = document.selectFirst("span.topcard__flavor--bullet");
            final String location = locationElement.text().trim();
            jobContent.setLocation(location);

            final Element descriptionElement = document.selectFirst("div.show-more-less-html__markup");
            final String description = descriptionElement.text().trim();
            jobContent.setDescription(description);
        } catch (IOException e) {
            logger.info(e.getMessage());
        }
        return jobContent;
    }
}
